package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.storage.film.DBFilmStorage;
import ru.com.practicum.filmorate.storage.film.FilmStorage;
import ru.com.practicum.filmorate.validator.FilmValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final JdbcTemplate jdbcTemplate;
    DBFilmStorage dbFilmStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public Film add(Film film) {
        FilmValidator.validate(film);
        Film receivedFilm = filmStorage.add(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(receivedFilm.getId(), film.getGenres());
        }
        return receivedFilm;
    }

    public Film update(Film film) {
        FilmValidator.validate(film);
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        return filmStorage.update(film);
    }

    public void addLike(Long id, Long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        filmStorage.addLike(id, userId);
        log.info("Фильм с id={} лайкнул пользователь {}", film.getId(), userId);
    }

    public void removeLike(Long id, Long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        if (!filmStorage.hasLikeFromUser(id, userId)) {
            throw new NotFoundException("Лайк пользователя " + userId + " фильму с id=" + id + " не найден");
        }
        filmStorage.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, film.getId());
    }

    public List<Film> getTop(Integer count, Long genreId, Integer year) {
        return getFilterFilmsByGenreId(getFilterFilmsByYear(filmStorage.getAll().stream(), year), genreId)
                .sorted((f1, f2) -> (dbFilmStorage.getFilmLikeId(f2.getId()) - dbFilmStorage.getFilmLikeId(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Stream<Film> getFilterFilmsByGenreId(Stream<Film> filmStream, Long genreId){
        return genreId == null ? filmStream
                : filmStream.filter(film -> film.getGenres().stream().anyMatch(g -> genreId.equals(g.getId())));
    }

    private Stream<Film> getFilterFilmsByYear(Stream<Film> filmStream, Integer year){
        return year == null ? filmStream : filmStream.filter(film -> year.equals(LocalDate.parse(film.getReleaseDate()).getYear()));
    }
}