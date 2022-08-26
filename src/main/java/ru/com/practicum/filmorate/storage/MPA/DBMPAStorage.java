package ru.com.practicum.filmorate.storage.MPA;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DBMPAStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    public DBMPAStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "SELECT m.id, " +
                                 "m.name " +
                          "FROM MPA_ratings AS m;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs));
    }

    @Override
    public MPA getById(Long id) throws NotFoundException {
        String sqlQuery = "SELECT m.id, " +
                                 "m.name " +
                          "FROM MPA_ratings AS m " +
                          "WHERE m.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMPA(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не существует"));
    }

    private MPA makeMPA(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new MPA(id, name);
    }

}
