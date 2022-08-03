package ru.com.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private int currId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        userValidate(user);
        user.setId(++currId);
        users.put(currId, user);
        log.info("Пользователь с id=" + user.getId() + "создан");
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException, NotFoundException {
        userValidate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь с id=" + user.getId() + " обновлен");
        } else {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " несуществует");
        }
        return user;
    }

    private void userValidate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты должен содержать символ '@'");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (LocalDate.parse(user.getBirthday()).isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Поскольку имя не было передано, вместо него будет использован логин");
            user.setName(user.getLogin());

        }
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(
            ValidationException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(
            NotFoundException exception
    ) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
