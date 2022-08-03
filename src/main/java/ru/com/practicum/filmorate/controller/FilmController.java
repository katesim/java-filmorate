package ru.com.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private int currId = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        filmValidate(film);
        film.setId(++currId);
        films.put(currId, film);
        log.info("Фильм с id=" + film.getId() + "создан");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        filmValidate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id=" + film.getId() + " обновлен");
        } else {
            log.warn("Фильм с id=" + film.getId() + " несуществует");
        }
        return film;
    }

    private void filmValidate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (LocalDate.parse(film.getReleaseDate()).isBefore(CINEMA_BIRTH)) {
            throw new ValidationException("Дата релиза — не раньше:" + CINEMA_BIRTH);
        }

        if (film.getDuration() <= 0.0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
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
}
