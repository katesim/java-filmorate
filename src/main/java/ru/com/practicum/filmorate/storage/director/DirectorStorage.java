package ru.com.practicum.filmorate.storage.director;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAll();

    Director getById(Long id) throws NotFoundException;

    List<Director> getByFilmId(Long filmId) throws NotFoundException;

    Director add(Director director);

    Director update(Director director);

    void delete(Director director);

}
