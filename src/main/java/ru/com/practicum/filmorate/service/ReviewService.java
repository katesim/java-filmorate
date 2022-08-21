package ru.com.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Review;
import ru.com.practicum.filmorate.storage.Review.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final static int TOP = 10;
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    /* получаем список отзывов по условиям:
    * - если не указан фильм, то все отзывы
    * - если не указано количество, то 10 отзывов
    * - и по условию фильм и количество */
    public List<Review> getAllByFilmId(Long filmId, Long count) {
        List<Review> allReviews = reviewStorage.getAll().stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .collect(Collectors.toList());

        if (filmId == null || filmId < 1) {
            return allReviews;
        } else if (count == null || count < 1) {
            return allReviews.stream()
                    .filter(review -> Objects.equals(review.getFilmId(), filmId))
                    .limit(TOP)
                    .collect(Collectors.toList());
        } else {
            return allReviews.stream()
                    .filter(review -> Objects.equals(review.getFilmId(), filmId))
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    public Review getById(long id) throws NotFoundException {
        return reviewStorage.getById(id);
    }

    public Review add(Review review) {
        filmService.getById(review.getFilmId()); // проверяем существование фильма
        userService.getById(review.getUserId()); // проверяем существование пользователя
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        getById(review.getReviewId()); // проверяем наличие отзыва
        return reviewStorage.update(review);
    }

    public void deleteById(long id) {
        reviewStorage.deleteById(id);
    }

    public void addLike(long reviewId, long userId) {
        getById(reviewId); // проверяем существование отзыва
        userService.getById(userId); // проверяем существование пользователя
        reviewStorage.addLike(reviewId, userId);
    }

    public void removeLike(long reviewId, long userId) {
        getById(reviewId); // проверяем существование отзыва
        userService.getById(userId); // проверяем существование пользователя
        reviewStorage.removeLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        getById(reviewId); // проверяем существование отзыва
        userService.getById(userId); // проверяем существование пользователя
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeDislike(long reviewId, long userId) {
        getById(reviewId); // проверяем существование отзыва
        userService.getById(userId); // проверяем существование пользователя
        reviewStorage.removeDislike(reviewId, userId);
    }
}