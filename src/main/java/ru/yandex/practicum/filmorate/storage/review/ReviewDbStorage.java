package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        final String sql = """
                    INSERT INTO reviews (film_id, user_id, content, is_positive, useful)
                    VALUES (?, ?, ?, ?, 0)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, review.getFilmId());
            ps.setLong(2, review.getUserId());
            ps.setString(3, review.getContent());
            ps.setBoolean(4, review.isPositive());
            return ps;
        }, kh);
        if (kh.getKey() == null) {
            throw new IllegalStateException("Не удалось получить сгенерированный id отзыва");
        }
        review.setId(kh.getKey().longValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        final String sql = """
                    UPDATE reviews
                       SET film_id = ?, user_id = ?, content = ?, is_positive = ?
                     WHERE id = ?
                """;
        int rows = jdbcTemplate.update(sql,
                review.getFilmId(),
                review.getUserId(),
                review.getContent(),
                review.isPositive(),
                review.getId());
        if (rows == 0) {
            throw new ReviewNotFoundException(review.getId());
        }
        return getReview(review.getId()).orElseThrow(() -> new ReviewNotFoundException(review.getId()));
    }

    @Override
    public void deleteReview(long id) {
        final String sql = "DELETE FROM reviews WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new ReviewNotFoundException(id);
        }
    }

    @Override
    public Optional<Review> getReview(long id) {
        final String sql = "SELECT id, film_id, user_id, content, is_positive, useful FROM reviews WHERE id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(sql, this::makeReview, id);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAllReviews() {
        final String sql = "SELECT id, film_id, user_id, content, is_positive, useful FROM reviews ORDER BY id";
        return jdbcTemplate.query(sql, this::makeReview);
    }

    @Override
    public List<Review> getMostUsefulReviews(long filmId, int limit) {

        final String sql = """
                    SELECT id, film_id, user_id, content, is_positive, useful
                    FROM reviews
                    WHERE film_id =?
                    ORDER BY useful DESC, id DESC
                    LIMIT ?
                """;
        return jdbcTemplate.query(sql, this::makeReview, filmId, limit);
    }


    @Override
    public void addLike(long reviewId, long userId) {
        final String updateSql = """
                UPDATE review_likes
                SET is_like = TRUE
                WHERE review_id = ? AND user_id = ?
                """;
        int rows = jdbcTemplate.update(updateSql, reviewId, userId);
        if (rows == 0) {
            final String insertSql = """
                    INSERT INTO review_likes (review_id, user_id, is_like)
                    VALUES (?, ?, TRUE)
                    """;
            jdbcTemplate.update(insertSql, reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        final String updateSql = """
                UPDATE review_likes
                SET is_like = FALSE
                WHERE review_id = ? AND user_id = ?
                """;
        int rows = jdbcTemplate.update(updateSql, reviewId, userId);
        if (rows == 0) {
            final String insertSql = """
                    INSERT INTO review_likes (review_id, user_id, is_like)
                    VALUES (?, ?, FALSE)
                    """;
            jdbcTemplate.update(insertSql, reviewId, userId);
        }
        updateUseful(reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        final String sql = """
                DELETE FROM review_likes
                WHERE review_id = ? AND user_id = ? AND is_like = TRUE
                """;
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        final String sql = """
                DELETE FROM review_likes
                WHERE review_id = ? AND user_id = ? AND is_like = FALSE
                """;
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public Optional<ReactionType> getUserReaction(long reviewId, long userId) {
        final String sql = """
                SELECT is_like
                FROM review_likes
                WHERE review_id = ? AND user_id = ?
                """;
        try {
            Boolean like = jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
            if (like == null) return Optional.empty();
            return Optional.of(like ? ReactionType.LIKE : ReactionType.DISLIKE);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public void recomputeUseful(long reviewId) {
        updateUseful(reviewId);
    }

    private void updateUseful(long reviewId) {
        final String sql = """
                UPDATE reviews
                SET useful = (
                    (SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = TRUE)
                    - (SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = FALSE)
                   )
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, reviewId, reviewId, reviewId);
    }


    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setFilmId(rs.getLong("film_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setContent(rs.getString("content"));
        r.setPositive(rs.getBoolean("is_positive"));
        r.setUseful(rs.getInt("useful"));
        return r;
    }
}
