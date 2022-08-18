package ru.com.practicum.filmorate.storage.genre;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(Long id) throws NotFoundException;

    List<Genre> getByFilmId(Long filmId) throws NotFoundException;

    void addAllToFilmId(Long filmId, List<Genre> genre);

    void deleteAllByFilmId(Long filmId);
}
