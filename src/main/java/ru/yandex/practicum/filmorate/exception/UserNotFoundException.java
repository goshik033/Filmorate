package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("Пользоваткль с id: " + id + " не найден");
    }
}
