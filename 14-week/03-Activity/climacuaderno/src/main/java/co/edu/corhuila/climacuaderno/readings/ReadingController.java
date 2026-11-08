package co.edu.corhuila.climacuaderno.readings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {
    private final ReadingRepository readings;

    public ReadingController(ReadingRepository readings) {
        this.readings = readings;
    }

    public record ReadingRequest(
            @NotBlank @Size(min = 2, max = 60) String zoneName,
            @NotNull @DecimalMin("-30.0") @DecimalMax("60.0") BigDecimal temperature,
            @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal humidity) { }

    @GetMapping
    public List<ReadingRepository.Reading> list(@AuthenticationPrincipal Jwt jwt) {
        return readings.forUser(Long.parseLong(jwt.getSubject()));
    }

    @PostMapping
    public ResponseEntity<ReadingRepository.Reading> create(@AuthenticationPrincipal Jwt jwt,
                                                              @Valid @RequestBody ReadingRequest body) {
        String zone = body.zoneName().trim();
        if (zone.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Zone name is too short");
        }
        ReadingRepository.Reading reading = readings.create(Long.parseLong(jwt.getSubject()), zone,
                body.temperature(), body.humidity());
        return ResponseEntity.status(HttpStatus.CREATED).body(reading);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        boolean removed = readings.deleteOwned(id, Long.parseLong(jwt.getSubject()));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Observation not found");
        }
        return ResponseEntity.noContent().build();
    }
}
