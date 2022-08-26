package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.com.practicum.filmorate.model.MPA;
import ru.com.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MPAController {
    private final MPAService mpaService;

    @GetMapping("/mpa")
    public List<MPA> findAll() {
        return mpaService.getAll();
    }

    @GetMapping("/mpa/{id}")
    public MPA findById(@PathVariable Long id) {
        return mpaService.getById(id);
    }

}
