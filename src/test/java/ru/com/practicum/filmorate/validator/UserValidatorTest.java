package ru.com.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private static final Long ID = 123L;
    private static final String NAME = "MyName";
    private static final String EMAIL = "myname@ya.ru";
    private static final String LOGIN = "login";
    private static final String BIRTHDAY = "1999-01-01";
    private static final List<Long> FRIENDS = new ArrayList<>();
    private static final String BLANK_STR = "   ";

    @Test
    void validate_userData_isValid() {
        User user = new User(ID, EMAIL, LOGIN, NAME, BIRTHDAY, FRIENDS);
        UserValidator.validate(user);
    }

    @Test
    void validate_emailIsEmpty_isNotValid() {
        User user = new User(ID, "", LOGIN, NAME, BIRTHDAY, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_emailIncorrect_isNotValid() {
        User user = new User(ID, "badEmail", LOGIN, NAME, BIRTHDAY, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_emailIsBlank_isNotValid() {
        User user = new User(ID, BLANK_STR, LOGIN, NAME, BIRTHDAY, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_loginIsEmpty_isNotValid() {
        User user = new User(ID, EMAIL, "", NAME, BIRTHDAY, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_loginIsBlank_isNotValid() {
        User user = new User(ID, EMAIL, BLANK_STR, NAME, BIRTHDAY, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void validate_birthdayInTheFuture_isNotValid() {
        LocalDate now = LocalDate.now();
        String nextDay = now.plusDays(1).format(DateTimeFormatter.ISO_DATE);
        User user = new User(ID, EMAIL, LOGIN, NAME, nextDay, FRIENDS);

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }
}
