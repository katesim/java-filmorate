package ru.com.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private List<Genre> genres;
    private MPA mpa;
    private List<Director> directors;
}
