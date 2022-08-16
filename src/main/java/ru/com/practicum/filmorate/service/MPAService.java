package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.storage.MPA.MPAStorage;


import java.util.List;

@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;

    public List<MPA> getAll() {
        return mpaStorage.getAll();
    }

    public MPA getById(Long id) throws NotFoundException {
        return mpaStorage.getById(id);
    }
}
