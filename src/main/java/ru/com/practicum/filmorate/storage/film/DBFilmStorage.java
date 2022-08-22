package ru.com.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.service.GenreService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository("filmStorage")
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    public DBFilmStorage(JdbcTemplate jdbcTemplate, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
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
                        "m.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN MPA_ratings AS m" +
                        "    ON m.id = f.mpa_id;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService));
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
                        "m.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN MPA_ratings AS m" +
                        "    ON m.id = f.mpa_id " +
                        "WHERE f.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), id)
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

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
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

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO likes_list (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM likes_list WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public boolean hasLikeFromUser(Long id, Long userId) {
        String sqlQuery = "SELECT COUNT(user_id) FROM likes_list WHERE film_id = ? AND user_id = ?;";
        int like = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id, userId);
        return like != 0;
    }

//    @Override
//    public List<Film> getTop(Integer count, Integer genreId, Integer year) {
//        if (genreId == null && year == null) {
//            String sqlQuery =
//                    "SELECT f.id, " +
//                            "f.name, " +
//                            "f.description, " +
//                            "f.release_date, " +
//                            "f.duration, " +
//                            "f.mpa_id, " +
//                            "m.name AS mpa_name " +
//                            "FROM films AS f " +
//                            "JOIN MPA_ratings AS m" +
//                            "    ON m.id = f.mpa_id " +
//                            "LEFT JOIN (SELECT film_id, " +
//                            "      COUNT(user_id) rate " +
//                            "      FROM likes_list " +
//                            "      GROUP BY film_id " +
//                            ") r ON f.id = r.film_id " +
//                            "ORDER BY r.rate DESC " +
//                            "LIMIT ?;";
//            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), count);
//        } else if (genreId != null && year == null) {
//            String sqlQuery =
//                    "SELECT f.id, " +
//                            "f.name, " +
//                            "f.description, " +
//                            "f.release_date, " +
//                            "f.duration, " +
//                            "f.mpa_id, " +
//                            "m.name AS mpa_name " +
//                            "FROM films AS f " +
//                            "JOIN MPA_ratings AS m" +
//                            "    ON m.id = f.mpa_id " +
//                            "LEFT JOIN (SELECT film_id, " +
//                            "      COUNT(user_id) rate " +
//                            "      FROM likes_list " +
//                            "      GROUP BY film_id " +
//                            ") r ON f.id = r.film_id " +
//                            "LEFT JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = ?" +
//                            "ORDER BY r.rate DESC " +
//                            "LIMIT ?;";
//            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), genreId, count);
//        } else if (genreId == null && year != null) {
//            String sqlQuery =
//                    "SELECT f.id, " +
//                            "f.name, " +
//                            "f.description, " +
//                            "f.release_date, " +
//                            "f.duration, " +
//                            "f.mpa_id, " +
//                            "m.name AS mpa_name " +
//                            "FROM films AS f " +
//                            "JOIN MPA_ratings AS m" +
//                            "    ON m.id = f.mpa_id " +
//                            "LEFT JOIN (SELECT film_id, " +
//                            "      COUNT(user_id) rate " +
//                            "      FROM likes_list " +
//                            "      GROUP BY film_id " +
//                            ") r ON f.id = r.film_id " +
//                            "WHERE extract(YEAR FROM cast(f.release_date AS DATE)) = ? " +
//                            "ORDER BY r.rate DESC " +
//                            "LIMIT ?;";
//            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), year, count);
//        } else {
//            String sqlQuery =
//                    "SELECT f.id, " +
//                            "f.name, " +
//                            "f.description, " +
//                            "f.release_date, " +
//                            "f.duration, " +
//                            "f.mpa_id, " +
//                            "m.name AS mpa_name " +
//                            "FROM films AS f " +
//                            "JOIN MPA_ratings AS m" +
//                            "    ON m.id = f.mpa_id " +
//                            "LEFT JOIN (SELECT film_id, " +
//                            "      COUNT(user_id) rate " +
//                            "      FROM likes_list " +
//                            "      GROUP BY film_id " +
//                            ") r ON f.id = r.film_id " +
//                            "LEFT JOIN films_genres AS fg ON f.id = fg.film_id WHERE fg.genre_id = ?" +
//                            "AND extract(YEAR FROM cast(f.release_date AS DATE)) = ? " +
//                            "ORDER BY r.rate DESC " +
//                            "LIMIT ?;";
//            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService), genreId, year, count);
//        }
//    }

    private Film makeFilm(ResultSet rs, GenreService genreService) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String releaseDate = rs.getDate("release_date").toString();
        int duration = rs.getInt("duration");
        List<Genre> genres = genreService.getByFilmId(id);
        MPA mpa = new MPA(
                rs.getLong("mpa_id"),
                rs.getString("mpa_name")
        );
        return new Film(id, name, description, releaseDate, duration, genres, mpa);
    }
}
