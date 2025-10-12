package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Optional<Review> getReview(long id);

    List<Review> getAllReviews();

    List<Review> getMostUsefulReviews(long filmId, int limit);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);

    Optional<ReactionType> getUserReaction(long reviewId, long userId);

    void recomputeUseful(long reviewId);

    enum ReactionType {LIKE, DISLIKE}
}
