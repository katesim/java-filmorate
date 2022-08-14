package ru.com.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private static final Long ID = 123L;
    private static final String NAME = "MyName";
    private static final String EMAIL = "myname@ya.ru";
    private static final String LOGIN = "login";
    private static final String BIRTHDAY = "1999-01-01";
    private static final String BLANK_STR = "   ";

    @Test
    void validate_userData_isValid() {
        User user = new User(ID, EMAIL, LOGIN, NAME, BIRTHDAY);
        UserValidator.validate(user);
    }

    @Test
    void validate_emailIsEmpty_isNotValid() {
        User user = new User(ID, "", LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_emailIncorrect_isNotValid() {
        User user = new User(ID, "badEmail", LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_emailIsBlank_isNotValid() {
        User user = new User(ID, BLANK_STR, LOGIN, NAME, BIRTHDAY);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_loginIsEmpty_isNotValid() {
        User user = new User(ID, EMAIL, "", NAME, BIRTHDAY);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_loginIsBlank_isNotValid() {
        User user = new User(ID, EMAIL, BLANK_STR, NAME, BIRTHDAY);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_birthdayInTheFuture_isNotValid() {
        LocalDate now = LocalDate.now();
        User user = new User(ID, EMAIL, LOGIN, NAME, now.plusDays(1).format(DateTimeFormatter.ISO_DATE));

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }
}
