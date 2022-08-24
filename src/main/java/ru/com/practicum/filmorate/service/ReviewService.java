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

    public List<Review> getAll() { // получаем все отзывы на все фильмы
        return reviewStorage.getAll().stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public List<Review> getByFilmId(long filmId, int limit) { // получаем отзывы к нужному фильму
        return reviewStorage.getByFilmId(filmId).stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
