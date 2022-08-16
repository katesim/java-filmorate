package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;
}
