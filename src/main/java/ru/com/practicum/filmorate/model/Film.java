package ru.com.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
public class Film {
    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private MPA mpa;
    private final List<Genre> genres = new ArrayList<>();
    private final List<Director> directors = new ArrayList<>();
}
