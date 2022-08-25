package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.storage.feed.FeedStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserService userService;

    public List<Event> getByUserId(Long userId) {
        userService.getById(userId);
        return feedStorage.getByUserId(userId);
    }

    public Event addEvent(Event event) {
        log.info(event.toString());
        return feedStorage.addEvent(event);
    }

}
