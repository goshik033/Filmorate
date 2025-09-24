package ru.yandex.practicum.filmorate.exeption;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("Пользоваткль с id: " + id + " не найден");
    }
}
