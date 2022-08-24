package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Film;
import ru.com.practicum.filmorate.model.Like;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.storage.user.UserStorage;
import ru.com.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

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

        List<Film> recommendations = new ArrayList<>();
        List<Like> allLikes = userStorage.getAllLikes();
        List<Long> userLikes = getUserLikes(userId, allLikes);
        List<Long> otherUserLikes = findMaxSimilarUser(userId, userLikes, allLikes);

        for (Long id : otherUserLikes) {
            if (!userLikes.contains(id)) {
                recommendations.add(filmService.getById(id));
            }
        }

        return recommendations;
    }

    private List<Long> findMaxSimilarUser(long userId, List<Long> userLikes, List<Like> allLikes) { // ищем пользователя с максимальным пересечением интересов
        long maxSimilar = 0;
        int maxSize = 0;
        List<Long> usersIds = userStorage.getUsersIds();

        for (Long id : usersIds) {
            if (userId != id) {
                int size = compareUsersLikes(userLikes, getUserLikes(id, allLikes));

                if (size > maxSize) {
                    maxSize = size;
                    maxSimilar = id;
                }
            }
        }

        return getUserLikes(maxSimilar, allLikes);
    }

    private int compareUsersLikes(List<Long> userLikes, List<Long> otherUserLikes) { // сравниваем лайки пользователей
        int size = 0;

        for (Long filmId : userLikes) {
            if (otherUserLikes.contains(filmId)) {
                size++;
            }
        }

        return size;
    }

    private List<Long> getUserLikes(long userId, List<Like> allLikes) { // получение списка лайков пользователя
        List<Long> userLikes = new ArrayList<>();

        for (Like like : allLikes) {
            if (like.getUserId() == userId) {
                userLikes.add(like.getFilmId());
            }
        }

        return userLikes;
    }
}
