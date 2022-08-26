package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.storage.user.UserStorage;
import ru.com.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmService filmService;

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

    public void makeFriends(Long id, Long friendId) throws NotFoundException {
        User user = getById(id);
        User friend = getById(friendId);
        userStorage.makeFriends(id, friendId);
        log.info("Пользователь {} теперь друг {}", friendId, id);
    }

    public void removeFriends(Long id, Long friendId) throws NotFoundException {
        userStorage.removeFriends(id, friendId);
        log.info("Пользователь {} больше не друг {}", friendId, id);
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        userStorage.getById(id);
        List<User> friends = new ArrayList<>();
        List<Long> friendsIds = userStorage.getUserFriendsById(id);
        if (friendsIds == null) {
            return friends;
        }
        for (Long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public void deleteUser(Long id) {
        userStorage.getById(id);
        userStorage.delete(id);
        log.info("Пользователь c id {} удален", id);
    }

    public List<Film> getRecommendations(Long userId) {
        userStorage.getById(userId);
        return filmService.getRecommendations(userId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> first = getAllFriends(id);
        List<User> second = getAllFriends(otherId);
        return first.stream().filter(second::contains).collect(Collectors.toList());
    }

}
