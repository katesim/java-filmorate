package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;
    private final List<Long> friends;
}
