package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private final Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public boolean removeLike(Long userId) {
        return likes.remove(userId);
    }

    public int countLikes() {
        return likes.size();
    }
}
