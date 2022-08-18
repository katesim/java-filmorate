package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Long id) throws NotFoundException {
        return genreStorage.getById(id);
    }

    public List<Genre> getByFilmId(Long filmId) throws NotFoundException {
        return genreStorage.getByFilmId(filmId);
    }

    public void updateForFilm(Long filmId, List<Genre> genres) {
        genreStorage.deleteAllByFilmId(filmId);
        genreStorage.addAllToFilmId(filmId, genres);
    }

}
