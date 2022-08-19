package ru.com.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Director;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DirectorValidatorTest {
    private static final Long ID = 123L;
    private static final String NAME = "MyName";
    private static final String BLANK_STR = "   ";


    @Test
    void validate_directorData_isValid() {
        Director director = new Director(ID, NAME);
        DirectorValidator.validate(director);
    }

    @Test
    void validate_nameIsEmpty_isNotValid() {
        Director director = new Director(ID, "");

        assertThrows(ValidationException.class, () -> DirectorValidator.validate(director));
    }

    @Test
    void validate_nameIsBlank_isNotValid() {
        Director director = new Director(ID, BLANK_STR);

        assertThrows(ValidationException.class, () -> DirectorValidator.validate(director));
    }

}
