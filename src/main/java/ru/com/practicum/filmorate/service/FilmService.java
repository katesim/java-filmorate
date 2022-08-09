package ru.com.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {
    @Autowired
    FilmStorage filmStorage;
    private final int TOP = 10;

    public Film addLike(Long userId, Long id) throws NotFoundException {
        Film film = filmStorage.getById(id);
        film.addLike(userId);
        filmStorage.update(film);
        log.info("Фильм с id={} лайкнул пользователь {}. Всего лайков:{}", film.getId(), userId, film.countLikes());
        return film;
    }

    public Film deleteLike(Long userId, Long id) throws NotFoundException {
        Film film = filmStorage.getById(id);
        boolean result = film.removeLike(userId);
        if (!result) {
            log.warn("У фильма с id={} не был удален лайк, тк текущее число лайков уже равно нулю", film.getId());
            return film;
        }
        filmStorage.update(film);
        log.info("Пользователь {} удалил лайк с фильма с id={}. Всего лайков:{}", userId, film.getId(), film.getLikes());
        return film;
    }

    public List<Film> getTop(int count) {
        if (count == 0) {
            count = TOP;
        }
        List<Film> films = filmStorage.getAll();
        films.sort(Comparator.comparingInt(Film::countLikes));
        return films.stream().limit(count).collect(Collectors.toList());
    }

}
