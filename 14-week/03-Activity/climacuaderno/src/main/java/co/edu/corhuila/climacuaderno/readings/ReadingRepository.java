package co.edu.corhuila.climacuaderno.readings;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReadingRepository {
    private final JdbcTemplate jdbc;

    public ReadingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Reading(long id, String zoneName, BigDecimal temperature, BigDecimal humidity,
                          Instant recordedAt, String assessment) { }

    private static final RowMapper<Reading> READING_MAPPER = (rs, index) -> {
        BigDecimal temperature = rs.getBigDecimal("temperature");
        BigDecimal humidity = rs.getBigDecimal("humidity");
        boolean balanced = temperature.compareTo(BigDecimal.valueOf(19)) >= 0
                && temperature.compareTo(BigDecimal.valueOf(27)) <= 0
                && humidity.compareTo(BigDecimal.valueOf(35)) >= 0
                && humidity.compareTo(BigDecimal.valueOf(65)) <= 0;
        return new Reading(rs.getLong("id"), rs.getString("zone_name"), temperature, humidity,
                rs.getObject("recorded_at", OffsetDateTime.class).toInstant(),
                balanced ? "balanced" : "review");
    };

    public List<Reading> forUser(long userId) {
        return jdbc.query("SELECT id, zone_name, temperature, humidity, recorded_at "
                + "FROM readings WHERE user_id = ? ORDER BY id DESC", READING_MAPPER, userId);
    }

    @Transactional
    public Reading create(long userId, String zoneName, BigDecimal temperature, BigDecimal humidity) {
        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO readings(user_id, zone_name, temperature, humidity, recorded_at) "
                            + "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, userId);
            statement.setString(2, zoneName);
            statement.setBigDecimal(3, temperature);
            statement.setBigDecimal(4, humidity);
            statement.setObject(5, OffsetDateTime.now(ZoneOffset.UTC));
            return statement;
        }, keys);
        long id = keys.getKey().longValue();
        return jdbc.queryForObject("SELECT id, zone_name, temperature, humidity, recorded_at "
                + "FROM readings WHERE id = ? AND user_id = ?", READING_MAPPER, id, userId);
    }

    public boolean deleteOwned(long readingId, long userId) {
        return jdbc.update("DELETE FROM readings WHERE id = ? AND user_id = ?", readingId, userId) > 0;
    }
}
