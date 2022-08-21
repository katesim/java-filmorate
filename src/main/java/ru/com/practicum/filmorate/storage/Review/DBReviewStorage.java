package ru.com.practicum.filmorate.storage.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE id = ?;", id);

        return getReview(filmRows);
    }

    @Override
    public Review add(Review review) {
        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?);";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        log.trace("Сохранен объект: {}", review);

        return getByContent(review.getContent());
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

    private Review getByContent(String content) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM reviews WHERE content = ?;", content);

        return getReview(filmRows);
    }

    private Review getReview(SqlRowSet filmRows) {
        if(filmRows.next()) {
            Review review = new Review(
                    filmRows.getLong("id"),
                    filmRows.getString("content"),
                    filmRows.getBoolean("is_positive"),
                    filmRows.getLong("user_id"),
                    filmRows.getLong("film_id"));

            loadLikes(review); // загружаем лайки
            loadDislikes(review); // загружаем дизлайки
            review.setUseful(calculateUseful(review)); // считаем полезность отзыва
            return review;
        } else {
            throw new NotFoundException("Отзыв не существует");
        }
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review(
                resultSet.getLong("id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"));

        loadLikes(review);
        loadDislikes(review);
        review.setUseful(calculateUseful(review));
        return review;
    }

    private long calculateUseful(Review review) { // счетчик рейтинга полезности
        long useful = 0;
        useful += review.getLikes().size();
        useful -= review.getDislikes().size();
        return useful;
    }

    private void loadLikes(Review review) {
        Collection<Long> reviewLikes = getReviewLikesFromTable(review.getReviewId());

        if (!reviewLikes.isEmpty()) {
            for (Long id : reviewLikes) {
                review.getLikes().add(id);
            }
        }
    }

    private Collection<Long> getReviewLikesFromTable(Long reviewId) {
        String sqlQuery = "SELECT user_id FROM review_likes WHERE review_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::getUserIdFromTableReviewLikes, reviewId);
    }

    private long getUserIdFromTableReviewLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }

    private void loadDislikes(Review review) {
        Collection<Long> reviewDislikes = getReviewDislikesFromTable(review.getReviewId());

        if (!reviewDislikes.isEmpty()) {
            for (Long id : reviewDislikes) {
                review.getDislikes().add(id);
            }
        }
    }

    private Collection<Long> getReviewDislikesFromTable(Long reviewId) {
        String sqlQuery = "SELECT user_id FROM review_dislikes WHERE review_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::getUserIdFromTableReviewDislikes, reviewId);
    }

    private long getUserIdFromTableReviewDislikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }
}