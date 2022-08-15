package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.storage.user.UserStorage;
import ru.com.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        UserValidator.validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Поскольку имя не было передано, вместо него будет использован логин");
            user.setName(user.getLogin());
        }

        return userStorage.add(user);
    }

    public User update(User user) {
        UserValidator.validate(user);
        return userStorage.update(user);
    }

    public void madeFriends(Long id, Long friendId) throws NotFoundException {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        user.addFriend(friendId);
        friend.addFriend(id);
        log.info("Пользователи {} и {} теперь друзья", id, friendId);
    }

    public void removeFriends(Long id, Long friendId) throws NotFoundException {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(id);
        log.info("Пользователи {} и {} больше не друзья", id, friendId);
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        List<User> friends = new ArrayList<>();
        Set<Long> friendsIds = userStorage.getById(id).getFriends();
        if (friendsIds == null) {
            return friends;
        }
        for (Long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }
}
