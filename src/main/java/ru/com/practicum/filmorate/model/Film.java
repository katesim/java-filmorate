package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
}
