package ru.com.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.storage.film.DBFilmStorage;
import ru.com.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    private final GenreStorage genreStorage;
    private final DBFilmStorage filmStorage;

    @Test
    void getById_valid_id_ValidGenre() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getById(1L));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getById_not_valid_id_empty() {
        assertThrows(NotFoundException.class, () -> genreStorage.getById(10L));
    }

    @Test
    void getAll_execute_6Items() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
    }

    @Test
    void getByFilmId_filmId_correctGenres() {
        Film film = Film.builder()
                .name("MyName")
                .description("my description")
                .duration(150)
                .releaseDate("1999-01-01")
                .mpa(MPA.builder().id(1L).build())
                .build();
        Long filmId = filmStorage.add(film).getId();
        List<Genre> testGenres = genreStorage.getAll().subList(0,5);
        genreStorage.addAllToFilmId(filmId, testGenres);
        List<Genre> genres = genreStorage.getByFilmId(filmId);
        assertThat(genres).hasSize(5).containsAll(testGenres);
    }

}
