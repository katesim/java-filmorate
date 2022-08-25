package ru.com.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository("reviewStorage")
@RequiredArgsConstructor
@Slf4j
public class DBReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll() {
        String sqlQuery = "SELECT * FROM reviews;";
        return jdbcTemplate.query(sqlQuery, this::makeReview);
    }

    @Override
    public Review getById(long id) throws NotFoundException {
        String sqlQuery = "SELECT * FROM reviews WHERE id = ?;";

        return jdbcTemplate.query(sqlQuery, this::makeReview, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + id + " не существует"));
    }

    @Override
    public Review add(Review review) {
        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setLong(3, review.getUserId());
            statement.setLong(4, review.getFilmId());
            return statement;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.trace("Сохранен объект: {}", review);

        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?;";

        jdbcTemplate.update(
                sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        log.trace("Обновлен объект: {}.", review);
        return getById(review.getReviewId());
    }

    @Override
    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM reviews WHERE id = ?;";

        Review review = getById(id); // проверяем существование отзыва
        jdbcTemplate.update(sqlQuery, id);
        log.trace("Удален объект: {}.", review);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id) VALUES (?, ?);";
    // проверяем наличие дизлайка от пользователя, если есть, то удаляем
        if (getReviewDislikesFromTable(reviewId).contains(userId)) {
            removeDislike(reviewId, userId);
        }

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String sqlQuery = "INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?);";
        // проверяем наличие лайка от пользователя, если есть, то удаляем
        if (getReviewLikesFromTable(reviewId).contains(userId)) {
            removeLike(reviewId, userId);
        }

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public List<Review> getByFilmId(long filmId) { // получаем все отзывы к фильму
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId);
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review(
                resultSet.getLong("id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"));

        review.setLikes(getReviewLikesFromTable(review.getReviewId()));
        review.setDislikes(getReviewDislikesFromTable(review.getReviewId()));
        review.setUseful(calculateUseful(review));
        return review;
    }

    private long calculateUseful(Review review) { // счетчик рейтинга полезности
        long useful = 0;
        useful += review.getLikes().size();
        useful -= review.getDislikes().size();
        return useful;
    }

    private List<Long> getReviewLikesFromTable(Long reviewId) {
        String sqlQuery = "SELECT user_id FROM review_likes WHERE review_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::getUserIdFromTableReviewLikes, reviewId);
    }

    private long getUserIdFromTableReviewLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }

    private List<Long> getReviewDislikesFromTable(Long reviewId) {
        String sqlQuery = "SELECT user_id FROM review_dislikes WHERE review_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::getUserIdFromTableReviewDislikes, reviewId);
    }

    private long getUserIdFromTableReviewDislikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }
}
