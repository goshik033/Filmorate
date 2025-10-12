package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(long id) {
        super("Отзыв с id: " + id + " не найден");

    }
}

