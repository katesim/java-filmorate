package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.storage.film.FilmStorage;
import ru.com.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final static int TOP = 10;
    private final FilmStorage filmStorage;
    private final GenreService genreService;
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
        if (film.getGenres() != null){
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
        if (film.getGenres() != null){
            genreService.updateForFilm(film.getId(), film.getGenres());
        }
        if (film.getDirectors() != null) {
            directorService.updateForFilm(film.getId(), film.getDirectors());
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
        if (! filmStorage.hasLikeFromUser(id, userId)){
            throw new NotFoundException("Лайк пользователя " + userId + " фильму с id=" + id + " не найден");
        }
        filmStorage.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк с фильма с id={}", userId, film.getId());
    }

    public List<Film> getTop(Integer count) {
        if (count == null) {
            count = TOP;
        }
        return filmStorage.getTop(count);
    }

}
