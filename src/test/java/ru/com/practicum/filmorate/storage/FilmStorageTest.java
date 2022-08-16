package ru.com.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.storage.film.DBFilmStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final DBFilmStorage filmStorage;

    @Test
    void getById_valid_id_ValidGenre() {
        Film testFilm = Film.builder()
                .name("MyName")
                .description("my description")
                .duration(150)
                .releaseDate("1999-01-01")
                .mpa(MPA.builder().id(1L).build())
                .build();

        Long filmId = filmStorage.add(testFilm).getId();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getById(filmId));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getById_not_valid_id_empty() {
        assertThrows(NotFoundException.class, () -> filmStorage.getById(10L));
    }

}
