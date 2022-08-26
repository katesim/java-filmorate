package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.service.FeedService;
import ru.com.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final UserService userService;

    @GetMapping("/users/{id}/feed")
    public List<Event> findById(@PathVariable Long id) {
        userService.getById(id);
        return feedService.getByUserId(id);
    }

}
