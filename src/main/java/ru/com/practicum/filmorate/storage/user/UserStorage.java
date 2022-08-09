package ru.com.practicum.filmorate.storage.user;

import ru.com.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    User getById(Long id);

    User add(User user);

    User update(User usesr);

    void delete(User user);
}
