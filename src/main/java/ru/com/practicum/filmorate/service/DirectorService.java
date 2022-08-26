package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Director;
import ru.com.practicum.filmorate.storage.director.DirectorStorage;
import ru.com.practicum.filmorate.validator.DirectorValidator;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director getById(Long id) throws NotFoundException {
        return directorStorage.getById(id);
    }

    public List<Director> getByFilmId(Long filmId) throws NotFoundException {
        return directorStorage.getByFilmId(filmId);
    }

    public Director add(Director director) throws ValidationException {
        DirectorValidator.validate(director);
        return directorStorage.add(director);
    }

    public Director update(Director director) throws ValidationException {
        DirectorValidator.validate(director);
        return directorStorage.update(director);
    }

    public void delete(Long id) {
        directorStorage.delete(id);
    }

    public void updateForFilm(Long filmId, List<Director> directors) {
        directorStorage.deleteAllByFilmId(filmId);
        if (directors != null) {
            directorStorage.addAllToFilmId(filmId, directors);
        }
    }

}
