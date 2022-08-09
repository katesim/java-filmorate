package ru.com.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.User;
import ru.com.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserStorage userStorage;

    public void addFriend(Long friendId, Long id) throws NotFoundException {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(id);
        user.addFriend(friendId);
        friend.addFriend(id);
        log.info("Пользователи {} и {} теперь друзья", friendId, id);
    }

    public void removeFriend(Long friendId, Long id) throws NotFoundException {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(id);
        user.removeFriend(friendId);
        friend.removeFriend(id);
        log.info("Пользователи {} и {} больше не друзья", friendId, id);
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        Set<Long> friendsIds = userStorage.getById(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }
}
