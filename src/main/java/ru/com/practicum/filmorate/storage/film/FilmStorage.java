package ru.com.practicum.filmorate.storage.film;

import ru.com.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film getById(Long id);

    Film add(Film film);

    Film update(Film film);

    void delete(Film film);

}