package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.service.FeedService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public List<Event> findById(@PathVariable Long id) {
        return feedService.getByUserId(id);
    }
}
