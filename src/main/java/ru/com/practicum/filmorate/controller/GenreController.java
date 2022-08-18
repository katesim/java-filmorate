package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.model.Genre;
import ru.com.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> findAll() {
        return genreService.getAll();
    }

    @GetMapping("/genres/{id}")
    public Genre findById(@PathVariable Long id) {
        return genreService.getById(id);
    }

}
