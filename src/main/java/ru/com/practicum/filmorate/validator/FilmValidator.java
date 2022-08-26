package ru.com.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    public static void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (LocalDate.parse(film.getReleaseDate()).isBefore(CINEMA_BIRTH)) {
            throw new ValidationException("Дата релиза — не раньше:" + CINEMA_BIRTH);
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

}
