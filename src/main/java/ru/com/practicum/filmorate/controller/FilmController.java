package ru.com.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.storage.film.FilmStorage;
import ru.com.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.com.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        FilmValidator.validate(film);
        filmStorage.add(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws ValidationException, NotFoundException {
        FilmValidator.validate(film);
        filmStorage.update(film);
        return film;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(
            ValidationException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(
            NotFoundException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
