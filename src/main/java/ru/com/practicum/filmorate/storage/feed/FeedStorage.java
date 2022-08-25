package ru.com.practicum.filmorate.storage.feed;

import ru.com.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> getByUserId(Long userId);

    Event addEvent(Event event);

}
