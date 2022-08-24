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

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    private static final Long ID = 123L;
    private static final String NAME = "MyName";
    private static final String DESCRIPTION = "my description";
    private static final String RELEASE_DATE = "1999-01-01";
    private static final int DURATION = 150;
    private static final List<Genre> GENRES = new ArrayList<>();
    private static final MPA MPA = new MPA(1L, "PG");
    private static final List<Director> DIRECTORS = new ArrayList<>();
    private static final String BLANK_STR = "   ";

    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);


    @Test
    void validate_filmData_isValid() {
        Film film = new Film(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRES, MPA, DIRECTORS);
        FilmValidator.validate(film);
    }

    @Test
    void validate_nameIsEmpty_isNotValid() {
        Film film = new Film(ID, "", DESCRIPTION, RELEASE_DATE, DURATION, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void validate_nameIsBlank_isNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, DURATION, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void validate_descriptionIsLongerThan200_isNotValid() {
        String longDescription = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль." +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время " +
                "«своего отсутствия», стал кандидатом Коломбани.";
        Film film = new Film(ID, BLANK_STR, longDescription, RELEASE_DATE, DURATION, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void validate_releaseDateIsTooEarly_isNotValid() {
        String releaseDate = CINEMA_BIRTH.minusDays(1).format(DateTimeFormatter.ISO_DATE);
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, releaseDate, DURATION, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void validate_durationIsNegative_isNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, -100, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void validate_durationIsZero_isNotValid() {
        Film film = new Film(ID, BLANK_STR, DESCRIPTION, RELEASE_DATE, 0, GENRES, MPA, DIRECTORS);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }
}
