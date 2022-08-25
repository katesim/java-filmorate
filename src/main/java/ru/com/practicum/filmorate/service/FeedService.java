package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.storage.feed.FeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserService userService;

    public List<Event> getByUserId(Long userId) {
        userService.getById(userId);
        return feedStorage.getByUserId(userId);
    }

    public Event addEvent(Event event) {
        return feedStorage.addEvent(event);
    }

}
