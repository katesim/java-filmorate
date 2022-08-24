package ru.com.practicum.filmorate.storage.user;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getById(Long id) throws NotFoundException;

    User add(User user);

    User update(User user);

    void delete(Long id);

    void makeFriends(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    List<Long> getUserFriendsById(Long userId);
}