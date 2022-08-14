package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.service.UserService;
import ru.com.practicum.filmorate.storage.user.UserStorage;
import ru.com.practicum.filmorate.validator.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User findById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ValidationException {
        UserValidator.validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Поскольку имя не было передано, вместо него будет использован логин");
            user.setName(user.getLogin());
        }

        return userService.add(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException, NotFoundException {
        UserValidator.validate(user);
        return userService.update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void madeFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.madeFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void removeFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriends(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        List<User> first = userService.getAllFriends(id);
        List<User> second = userService.getAllFriends(otherId);
        return first.stream().filter(second::contains).collect(Collectors.toList());
    }

}
