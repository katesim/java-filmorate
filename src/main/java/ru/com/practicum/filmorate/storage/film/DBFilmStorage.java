package ru.com.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.model.MPARating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository("filmStorage")
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public DBFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa " +
                        "FROM films AS f " +
                        "JOIN MPA_ratings AS m" +
                        "    ON m.id = f.mpa_id;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getById(Long id) throws NotFoundException {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa " +
                "FROM films AS f " +
                "JOIN MPA_ratings AS m" +
                "    ON m.id = f.mpa_id " +
                "WHERE f.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не существует"));
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        return getById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return getById(film.getId());
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String releaseDate = rs.getDate("release_date").toString();
        int duration = rs.getInt("duration");
        List<Genre> genres = new ArrayList<>();
        MPARating mpaRating = new MPARating(
                rs.getLong("mpa_id"),
                rs.getString("mpa")
        );
        return new Film(id, name, description, releaseDate, duration, genres, mpaRating);
    }
}
