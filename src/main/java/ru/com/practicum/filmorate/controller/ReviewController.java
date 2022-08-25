package ru.com.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.model.EventTypes;
import ru.com.practicum.filmorate.model.OperationTypes;
import ru.com.practicum.filmorate.model.Review;
import ru.com.practicum.filmorate.service.FeedService;
import ru.com.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final FeedService feedService;

    @GetMapping("/reviews")
    public List<Review> getFilmsReviews(@RequestParam(required = false) @Positive Long filmId,
                                        @RequestParam(defaultValue = "10", required = false) @Positive int count) {
        if (filmId == null) {
            return reviewService.getAll();
        }
        return reviewService.getByFilmId(filmId, count);
    }

    @GetMapping("/reviews/{id}")
    public Review getById(@PathVariable long id) throws NotFoundException {
        return reviewService.getById(id);
    }

    @PostMapping("/reviews")
    public Review add(@Valid @RequestBody Review review) {
        Review createdReview = reviewService.add(review);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(createdReview.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.ADD)
                .entityId(createdReview.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
        return createdReview;
    }

    @PutMapping("/reviews")
    public Review update(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.update(review);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(updatedReview.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.UPDATE)
                .entityId(updatedReview.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
        return updatedReview;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteById(@PathVariable long id) {
        Review review = reviewService.deleteById(id);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(EventTypes.REVIEW)
                .operation(OperationTypes.REMOVE)
                .entityId(review.getReviewId())
                .eventId(0L)
                .build();
        feedService.addEvent(event);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeLike(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeDislike(id, userId);
    }
}
