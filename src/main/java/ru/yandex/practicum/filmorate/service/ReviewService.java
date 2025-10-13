package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage,
                         FilmStorage filmStorage, EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public Review createReview(Review review) {
        if (review.getFilmId() <= 0) {
            throw new IncorrectParameterException("filmId должен быть положительным: " + review.getFilmId(), "filmId");
        }
        if (review.getUserId() <= 0) {
            throw new IncorrectParameterException("userId должен быть положительным: " + review.getUserId(), "userId");
        }
        userStorage.getUser(review.getUserId())
                .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        filmStorage.getFilm(review.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException(review.getFilmId()));
        eventService.publish(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getId());

        return reviewStorage.createReview(review);
    }


    public Review updateReview(Review review) {
        if (review.getId() <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + review.getId(), "reviewId");
        }
        if (review.getFilmId() <= 0) {
            throw new IncorrectParameterException("filmId должен быть положительным: " + review.getFilmId(), "filmId");
        }
        if (review.getUserId() <= 0) {
            throw new IncorrectParameterException("userId должен быть положительным: " + review.getUserId(), "userId");
        }
        userStorage.getUser(review.getUserId())
                .orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        filmStorage.getFilm(review.getFilmId())
                .orElseThrow(() -> new FilmNotFoundException(review.getFilmId()));
        reviewStorage.getReview(review.getId())
                .orElseThrow(() -> new ReviewNotFoundException(review.getId()));

        eventService.publish(review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getId());

        return reviewStorage.updateReview(review);
    }


    public void deleteReview(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "reviewId");
        }
        Review r = reviewStorage.getReview(id).orElseThrow(() -> new ReviewNotFoundException(id));

        eventService.publish(r.getUserId(), EventType.REVIEW, Operation.DELETE, id);

        reviewStorage.deleteReview(id);
    }


    public Review getReview(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "reviewId");
        }
        return reviewStorage.getReview(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }


    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public List<Review> getTopUsefulReviews(long filmId, int limit) {
        if (filmId <= 0) {
            throw new IncorrectParameterException("filmId должен быть положительным: " + filmId, "filmId");
        }
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        return reviewStorage.getMostUsefulReviews(filmId, limit);
    }

    public Review addLike(long reviewId, long userId) {
        reviewStorage.addLike(reviewId, userId);

        eventService.publish(userId, EventType.REVIEW, Operation.LIKE, reviewId);

        return reviewStorage.getReview(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));

    }

    public Review addDislike(long reviewId, long userId) {
        reviewStorage.addDislike(reviewId, userId);

        eventService.publish(userId, EventType.REVIEW, Operation.DISLIKE, reviewId);

        return reviewStorage.getReview(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    public void deleteLike(long reviewId, long userId) {

        eventService.publish(userId, EventType.REVIEW, Operation.UNLIKE, reviewId);

        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {

        eventService.publish(userId, EventType.REVIEW, Operation.UNDISLIKE, reviewId);

        reviewStorage.deleteDislike(reviewId, userId);
    }

}
