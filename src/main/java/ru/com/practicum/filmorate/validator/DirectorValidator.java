package ru.com.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Director;


@Slf4j
public class DirectorValidator {
    public static void validate(Director director) throws ValidationException {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
    }
}
