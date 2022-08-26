package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.SortingTypes;
import ru.com.practicum.filmorate.storage.film.DBFilmStorage;
import ru.com.practicum.filmorate.storage.film.FilmStorage;
import ru.com.practicum.filmorate.validator.FilmValidator;

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
    private final DBFilmStorage dbFilmStorage;
    private final DirectorService directorService;

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
        if (film.getDirectors() != null) {
            directorService.updateForFilm(receivedFilm.getId(), film.getDirectors());
        }
        return receivedFilm;
    }

    public Film update(Film film) throws NotFoundException {
        FilmValidator.validate(film);
        filmStorage.getById(film.getId());
        if (film.getGenres() != null) {
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        directorService.updateForFilm(film.getId(), film.getDirectors());
        return filmStorage.update(film);
    }

    public void addLike(Long id, Long userId, int rating) throws NotFoundException {
        Film film = filmStorage.getById(id);

        if (rating < 11) {
            filmStorage.addLike(id, userId, rating);
        }

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
                .sorted((f1, f2) ->
                        (int) (dbFilmStorage.getFilmRating(f2.getId()) - dbFilmStorage.getFilmRating(f1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Stream<Film> getFilterFilmsByGenreId(Stream<Film> filmStream, Long genreId) {
        return genreId == null ? filmStream
                : filmStream.filter(film -> film.getGenres().stream().anyMatch(g -> genreId.equals(g.getId())));
    }

    private Stream<Film> getFilterFilmsByYear(Stream<Film> filmStream, Integer year) {
        return year == null ? filmStream : filmStream
                .filter(film -> year.equals(LocalDate.parse(film.getReleaseDate()).getYear()));
    }

    public List<Film> getCommonFilms(long userId, long friendId) throws NotFoundException {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.getById(filmId);
        filmStorage.delete(filmId);
        log.info("Фильм c id {} удален", filmId);
    }

    public List<Film> getFilmsByDirectorId(Long directorId, SortingTypes sortBy) throws NotFoundException {
        directorService.getById(directorId);
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    public List<Film> getRecommendations(Long userId) {
        return filmStorage.getRecommendations(userId);
    }

    public List<Film> searchFilms(String query, String by) {
        switch (by) {
            case "director":
                return filmStorage.searchFilms(query, "");
            case "title":
                return filmStorage.searchFilms("", query);
            case "director,title":
            case "title,director":
                return filmStorage.searchFilms(query, query);
            default:
                throw new IllegalStateException("Unexpected value: " + by);
        }
    }

}
