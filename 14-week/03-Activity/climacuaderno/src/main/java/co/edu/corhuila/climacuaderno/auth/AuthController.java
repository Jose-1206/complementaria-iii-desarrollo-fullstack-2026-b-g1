package co.edu.corhuila.climacuaderno.auth;

import co.edu.corhuila.climacuaderno.config.SecurityConfiguration;
import co.edu.corhuila.climacuaderno.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final long TOKEN_SECONDS = 30 * 60;
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtEncoder tokens;

    public AuthController(UserRepository users, PasswordEncoder passwords, JwtEncoder tokens) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    public record RegistrationRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 10, max = 128) String password) { }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) { }
    public record TokenResponse(String accessToken, String tokenType, long expiresIn) { }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "version", "1.0.0-java");
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegistrationRequest body) {
        String email = normalizeEmail(body.email());
        try {
            users.create(email, passwords.encode(body.password()));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Account created"));
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest body) {
        UserRepository.User user = users.findByEmail(normalizeEmail(body.email()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwords.matches(body.password(), user.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(SecurityConfiguration.TOKEN_ISSUER)
                .subject(Long.toString(user.id()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TOKEN_SECONDS))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String accessToken = tokens.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(accessToken, "bearer", TOKEN_SECONDS);
    }

    @GetMapping("/me")
    public Map<String, String> me(@AuthenticationPrincipal Jwt jwt) {
        long id = Long.parseLong(jwt.getSubject());
        UserRepository.User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
        return Map.of("email", user.email());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
