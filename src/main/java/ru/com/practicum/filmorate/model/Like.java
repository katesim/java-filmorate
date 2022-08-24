package ru.com.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Like { // создаем модель для удобства работы с лайками пользователей
    private final long userId;
    private final long filmId;
}