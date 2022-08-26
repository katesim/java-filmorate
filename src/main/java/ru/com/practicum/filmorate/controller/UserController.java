package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.service.UserService;

import java.util.List;

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
        return userService.add(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException, NotFoundException {
        return userService.update(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void makeFriends(@PathVariable Long id, @PathVariable Long friendId) throws NotFoundException {
        userService.makeFriends(id, friendId);
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
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping(value = "/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping(value = "/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        return userService.getRecommendations(id);
    }

}
