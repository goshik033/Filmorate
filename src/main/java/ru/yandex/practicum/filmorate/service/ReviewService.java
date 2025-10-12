package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        return reviewStorage.updateReview(review);
    }


    public void deleteReview(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "filmId");
        }
        reviewStorage.getReview(id).orElseThrow(() -> new ReviewNotFoundException(id));

        reviewStorage.deleteReview(id);
    }


    public Review getReview(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "filmId");
        }
        return reviewStorage.getReview(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }


    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }
}
