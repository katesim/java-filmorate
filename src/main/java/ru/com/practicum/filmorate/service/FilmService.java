package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.storage.film.FilmStorage;
import ru.com.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final static int TOP = 10;
    private final FilmStorage filmStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public Film add(Film film) {
        FilmValidator.validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        FilmValidator.validate(film);
        return filmStorage.update(film);
    }

    public Film addLike(Long id, Long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        film.addLike(userId);
        filmStorage.update(film);
        log.info("Фильм с id={} лайкнул пользователь {}. Всего лайков:{}", film.getId(), userId, film.countLikes());
        return film;
    }

    public Film removeLike(Long id, Long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        boolean result = film.removeLike(userId);
        if (!result) {
            throw new NotFoundException("Лайк пользователя " + userId + " фильму с id=" + id + " не найден");
        }
        filmStorage.update(film);
        log.info("Пользователь {} удалил лайк с фильма с id={}. Всего лайков:{}",
                userId, film.getId(), film.getLikes());
        return film;
    }

    public List<Film> getTop(Integer count) {
        if (count == null) {
            count = TOP;
        }
        List<Film> films = filmStorage.getAll();
        films.sort(Comparator.comparingInt(Film::countLikes).reversed());
        return films.stream().limit(count).collect(Collectors.toList());
    }

}
