package ru.com.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DBDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBDirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery =
                "SELECT d.id, " +
                       "d.name " +
                "FROM directors AS d;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getById(Long id) throws NotFoundException {
        String sqlQuery =
                "SELECT d.id, " +
                        "d.name " +
                        "FROM directors AS d " +
                        "WHERE d.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Режиссёра с id=" + id + " не существует"));
    }

    @Override
    public List<Director> getByFilmId(Long filmId) throws NotFoundException {
        String sqlQuery =
                "SELECT d.id, " +
                        "d.name " +
                        "FROM films_directors AS fd " +
                        "JOIN directors AS d ON fd.director_id = d.id " +
                        "WHERE fd.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?);";
        jdbcTemplate.update(sqlQuery, director.getName());
        return getById(director.getId());
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                director.getId()
        );
        return getById(director.getId());
    }

    @Override
    public void delete(Director director) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, director.getId());
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Director(id, name);
    }
}
