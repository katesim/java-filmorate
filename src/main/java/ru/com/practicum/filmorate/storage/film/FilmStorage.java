package ru.com.practicum.filmorate.storage.film;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film getById(Long id) throws NotFoundException;

    Film add(Film film);

    Film update(Film film);

    void delete(Film film);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    boolean hasLikeFromUser(Long id, Long userId);

   // List<Film> getTop(Integer count, Integer genreId, Integer year);
}
