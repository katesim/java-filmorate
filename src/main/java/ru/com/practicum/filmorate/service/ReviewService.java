package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Review;
import ru.com.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public List<Review> getFilmsReviews(Long filmId, int count) {
        if (filmId == null) {
            return getAll();
        }
        return getByFilmId(filmId, count);
    }

    public Review getById(long id) throws NotFoundException {
        return reviewStorage.getById(id);
    }

    public Review add(Review review) {
        filmService.getById(review.getFilmId());
        userService.getById(review.getUserId());
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        getById(review.getReviewId());
        return reviewStorage.update(review);
    }

    public Review deleteById(long id) {
        Review review = getById(id);
        reviewStorage.deleteById(id);
        return review;
    }

    public void addLike(long reviewId, long userId) {
        getById(reviewId);
        userService.getById(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void removeLike(long reviewId, long userId) {
        getById(reviewId);
        userService.getById(userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        getById(reviewId);
        userService.getById(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeDislike(long reviewId, long userId) {
        getById(reviewId);
        userService.getById(userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private List<Review> getAll() {
        return reviewStorage.getAll().stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    private List<Review> getByFilmId(long filmId, int limit) {
        return reviewStorage.getByFilmId(filmId).stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

}
