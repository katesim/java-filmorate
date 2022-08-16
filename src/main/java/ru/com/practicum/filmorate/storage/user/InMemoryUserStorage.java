package ru.com.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Long currId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) throws NotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " несуществует");
        }
        return user;
    }

    @Override
    public User add(User user) {
        user.setId(++currId);
        users.put(currId, user);
        log.info("Пользователь с id={} создан", user.getId());
        return user;
    }

    @Override
    public User update(User user) throws NotFoundException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь с id={} обновлен", user.getId());
        } else {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " несуществует");
        }
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
        log.info("Пользователь с id={} удален", user.getId());
    }

    @Override
    public void madeFriends(Long userId, Long friendId) {

    }

    @Override
    public void removeFriends(Long userId, Long friendId) {

    }

    @Override
    public List<Long> getUserFriendsById(Long userId) {
        return new ArrayList<>();
    }
}
