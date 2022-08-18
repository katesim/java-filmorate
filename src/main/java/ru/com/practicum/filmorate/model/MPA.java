package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;


@Data
@AllArgsConstructor
@SuperBuilder
public class MPA {
    private Long id;
    private String name;
}
