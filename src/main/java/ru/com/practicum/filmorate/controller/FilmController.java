package ru.com.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private int currId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        FilmValidator.validate(film);
        film.setId(++currId);
        films.put(currId, film);
        log.info("Фильм с id=" + film.getId() + "создан");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws ValidationException, NotFoundException {
        FilmValidator.validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id=" + film.getId() + " обновлен");
        } else {
            throw new NotFoundException("Фильм с id=" + film.getId() + " несуществует");
        }
        return film;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(
            ValidationException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(
            NotFoundException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
