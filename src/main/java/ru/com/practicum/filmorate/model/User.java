package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;
    private Set<Long> friends;

    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public boolean removeFriend(Long userId) {
        return friends.remove(userId);
    }
}
