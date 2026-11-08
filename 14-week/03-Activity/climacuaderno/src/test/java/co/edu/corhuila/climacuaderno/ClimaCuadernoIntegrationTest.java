package co.edu.corhuila.climacuaderno;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=only-for-integration-tests-a-long-random-key-123456",
        "DB_JDBC_URL=jdbc:h2:mem:climacuaderno_tests;DB_CLOSE_DELAY=-1",
        "CORS_ORIGINS=http://localhost:5500"
})
class ClimaCuadernoIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String newEmail() {
        return "field-" + UUID.randomUUID() + "@example.com";
    }

    private String registerAndLogin(String email) throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Credentials(email, "TestPass!1234"))))
                .andExpect(status().isCreated());
        MvcResult login = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Credentials(email, "TestPass!1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type", equalTo("bearer")))
                .andReturn();
        return json.readTree(login.getResponse().getContentAsString()).get("access_token").asText();
    }

    record Credentials(String email, String password) { }

    @Test
    void registerLoginAndProfileWorkEndToEnd() throws Exception {
        String email = newEmail();
        String token = registerAndLogin(email);
        mvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo(email)));
    }

    @Test
    void invalidCredentialsReturn401() throws Exception {
        String email = newEmail();
        registerAndLogin(email);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Credentials(email, "WrongPassword123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail", equalTo("Invalid email or password")));
    }

    @Test
    void missingAndMalformedTokensAreRejected() throws Exception {
        mvc.perform(get("/api/readings"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/readings").header("Authorization", "Bearer forged.jwt.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ownerCanWriteListAndDeleteReading() throws Exception {
        String token = registerAndLogin(newEmail());
        MvcResult created = mvc.perform(post("/api/readings")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"zone_name\":\"Laboratory east\",\"temperature\":23.5,\"humidity\":56}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assessment", equalTo("balanced")))
                .andExpect(jsonPath("$.zone_name", equalTo("Laboratory east")))
                .andReturn();
        long id = json.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/readings").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(delete("/api/readings/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/readings").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void anotherAccountCannotAccessOrDeleteAnObservation() throws Exception {
        String owner = registerAndLogin(newEmail());
        String other = registerAndLogin(newEmail());
        MvcResult result = mvc.perform(post("/api/readings")
                .header("Authorization", "Bearer " + owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"zone_name\":\"Private patio\",\"temperature\":31,\"humidity\":45}"))
                .andExpect(status().isCreated()).andReturn();
        JsonNode created = json.readTree(result.getResponse().getContentAsString());
        mvc.perform(get("/api/readings").header("Authorization", "Bearer " + other))
                .andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(delete("/api/readings/{id}", created.get("id").asLong())
                .header("Authorization", "Bearer " + other))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidTemperatureIsRejected() throws Exception {
        String token = registerAndLogin(newEmail());
        mvc.perform(post("/api/readings")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"zone_name\":\"Garden\",\"temperature\":200,\"humidity\":45}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void duplicateEmailReturnsConflict() throws Exception {
        String email = newEmail();
        registerAndLogin(email);
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Credentials(email, "TestPass!1234"))))
                .andExpect(status().isConflict());
    }

    @Test
    void corsAllowsConfiguredOriginAndBlocksOthers() throws Exception {
        mvc.perform(options("/api/readings")
                .header("Origin", "http://localhost:5500")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5500"));
        mvc.perform(options("/api/readings")
                .header("Origin", "https://not-allowed.example")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
