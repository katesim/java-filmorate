package ru.com.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.*;
import ru.com.practicum.filmorate.service.DirectorService;
import ru.com.practicum.filmorate.service.GenreService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Repository("filmStorage")
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;
    private final DirectorService directorService;

    public DBFilmStorage(JdbcTemplate jdbcTemplate, GenreService genreService, DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.id, " +
                                 "f.name, " +
                                 "f.description, " +
                                 "f.release_date, " +
                                 "f.duration, " +
                                 "f.mpa_id, " +
                                 "m.name AS mpa_name " +
                          "FROM films AS f " +
                          "JOIN MPA_ratings AS m ON m.id = f.mpa_id;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService, directorService));
    }

    @Override
    public Film getById(Long id) throws NotFoundException {
        String sqlQuery = "SELECT f.id, " +
                                 "f.name, " +
                                 "f.description, " +
                                 "f.release_date, " +
                                 "f.duration, " +
                                 "f.mpa_id, " +
                                 "m.name AS mpa_name " +
                          "FROM films AS f " +
                          "JOIN MPA_ratings AS m ON m.id = f.mpa_id " +
                          "WHERE f.id = ?;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService, directorService), id)
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
    public void delete(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
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

    @Override
    public List<Film> getFilmsByDirectorId(Long id, SortingTypes sortBy) {
        String sqlQuery;
        switch (sortBy) {
            case YEAR:
                sqlQuery = "SELECT f.id, " +
                                  "f.name, " +
                                  "f.description, " +
                                  "f.release_date, " +
                                  "f.duration, " +
                                  "f.mpa_id, " +
                                  "m.name AS mpa_name " +
                           "FROM films_directors AS fd " +
                           "JOIN MPA_ratings AS m ON m.id = f.mpa_id " +
                           "JOIN films AS f ON f.id = fd.film_id " +
                           "WHERE fd.director_id = ?" +
                           "ORDER BY f.release_date;";
                break;
            case LIKES:
                sqlQuery = "SELECT f.id, " +
                                  "f.name, " +
                                  "f.description, " +
                                  "f.release_date, " +
                                  "f.duration, " +
                                  "f.mpa_id, " +
                                  "m.name AS mpa_name " +
                           "FROM films_directors AS fd " +
                           "JOIN MPA_ratings AS m ON m.id = f.mpa_id " +
                           "JOIN films AS f ON f.id = fd.film_id " +
                           "LEFT JOIN (SELECT film_id, " +
                                             "COUNT(user_id) rate " +
                                      "FROM likes_list " +
                                      "GROUP BY film_id) r ON fd.id = r.film_id " +
                           "WHERE fd.director_id = ?" +
                           "ORDER BY r.rate DESC ";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortBy);
        }
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genreService, directorService), id);
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        List<Film> recommendations = new ArrayList<>();

        String similarUserQuery = "SELECT user_id, COUNT(film_id) as c " +
                                  "FROM likes_list " +
                                  "WHERE film_id IN (SELECT film_id " +
                                                    "FROM likes_list " +
                                                    "WHERE user_id = ?) AND user_id != ? " +
                                  "GROUP BY user_id " +
                                  "ORDER BY c DESC " +
                                  "LIMIT 1;";

        String recommendedFilmsQuery = "SELECT f.id, " +
                                              "f.name, " +
                                              "f.description, " +
                                              "f.release_date, " +
                                              "f.duration, " +
                                              "f.mpa_id, " +
                                              "m.name AS mpa_name " +
                                       "FROM likes_list AS l " +
                                       "JOIN films AS f ON f.id = l.film_id " +
                                       "JOIN MPA_ratings AS m ON m.id = f.mpa_id " +
                                       "WHERE l.film_id NOT IN (SELECT film_id " +
                                                               "FROM likes_list " +
                                                               "WHERE user_id = ?) AND l.user_id = ? ;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(similarUserQuery, userId, userId);

        if (!rowSet.next()) {
            return recommendations;
        }

        Long similarUserId = rowSet.getLong("user_id");

        return jdbcTemplate.query(recommendedFilmsQuery,
                (rs, rowNum) -> makeFilm(rs, genreService, directorService),
                userId, similarUserId);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery = "SELECT film_id " +
                          "FROM likes_list " +
                          "WHERE user_id = ? " +
                          "INTERSECT SELECT film_id " +
                          "FROM likes_list " +
                          "WHERE user_id = ?" +
                          "GROUP BY user_id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        List<Film> commonFilms = new ArrayList<>();
        while (rowSet.next()) {
            commonFilms.add(getById(rowSet.getLong("film_id")));
        }
        return commonFilms;
    }

    @Override
    public List<Film> searchFilms(String substring, String by) {
        String insertParameter;
        switch (by) {
            case "director":
                insertParameter = "(LOWER(d.name) LIKE %s) ";
                insertParameter = String.format(insertParameter, "'%" + substring.toLowerCase(Locale.ROOT) + "%'");
                break;
            case "title":
                insertParameter = "(LOWER(f.name) LIKE %s) ";
                insertParameter = String.format(insertParameter, "'%" + substring.toLowerCase(Locale.ROOT) + "%'");
                break;
            case "director,title":
            case "title,director":
                insertParameter = "(LOWER(d.name) LIKE %1$s) OR (LOWER(f.name) LIKE %1$s) ";
                insertParameter = String.format(insertParameter, "'%" + substring.toLowerCase(Locale.ROOT) + "%'");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + by);
        }
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa_name " +
                 "FROM films AS f " +
                 "JOIN MPA_ratings AS m ON m.id = f.mpa_id " +
                 "LEFT JOIN films_directors AS fd ON f.id = fd.film_id " +
                 "LEFT JOIN directors AS d ON fd.director_id = d.id " +
                 "LEFT JOIN likes_list AS l ON f.id = l.film_id " +
                 "WHERE " + insertParameter +
                 "GROUP BY f.id " +
                 "ORDER BY COUNT(l.user_id) DESC;";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> makeFilm(rs, genreService, directorService));
    }

    private Film makeFilm(ResultSet rs, GenreService genreService,
                          DirectorService directorService) throws SQLException {
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
        List<Director> directors = directorService.getByFilmId(id);
        Film film = new Film(id, name, description, releaseDate, duration, mpa);
        film.getGenres().addAll(genres);
        film.getDirectors().addAll(directors);
        return film;
    }

    public int getFilmLikeId(long film) {
        String sqlQuery = "SELECT user_id FROM likes_list WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::createLikeId, film).size();
    }

    private long createLikeId(ResultSet rs, int rowNum) throws SQLException {
        String user_id = "user_id";
        return rs.getLong(user_id);
    }

}
