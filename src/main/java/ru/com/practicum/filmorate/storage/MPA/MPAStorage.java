package ru.com.practicum.filmorate.storage.MPA;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAStorage {
    List<MPA> getAll();

    MPA getById(Long id) throws NotFoundException;

}
