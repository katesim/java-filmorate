package ru.com.practicum.filmorate.storage.Review;

import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    List<Review> getAll();

    Review getById(long id) throws NotFoundException;

    Review add(Review review);

    Review update(Review review);

    void deleteById(long id);

    void addLike(long reviewId, long userId);

    void removeLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);
}