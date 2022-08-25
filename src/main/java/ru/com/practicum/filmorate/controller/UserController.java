package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.exception.ValidationException;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.model.EventTypes;
import ru.com.practicum.filmorate.model.OperationTypes;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.service.FeedService;
import ru.com.practicum.filmorate.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

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
        Event event = Event.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(id)
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.ADD)
                .entityId(friendId)
                .eventId(0L)
                .build();
        feedService.addEvent(event);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void removeFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriends(id, friendId);
        Event event = Event.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(id)
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.REMOVE)
                .entityId(friendId)
                .eventId(0L)
                .build();
        feedService.addEvent(event);
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

    @DeleteMapping(value = "/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
