package co.edu.corhuila.climacuaderno.users;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void create(String email, String encodedPassword) {
        jdbc.update("INSERT INTO users(email, password_hash) VALUES (?, ?)", email, encodedPassword);
    }

    public Optional<User> findByEmail(String email) {
        List<User> found = jdbc.query(
                "SELECT id, email, password_hash FROM users WHERE email = ?",
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"), rs.getString("password_hash")),
                email);
        return found.stream().findFirst();
    }

    public Optional<User> findById(long id) {
        List<User> found = jdbc.query(
                "SELECT id, email, password_hash FROM users WHERE id = ?",
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"), rs.getString("password_hash")),
                id);
        return found.stream().findFirst();
    }

    public record User(long id, String email, String passwordHash) { }
}
