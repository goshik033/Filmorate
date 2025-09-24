package ru.yandex.practicum.filmorate.exeption;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(long id) {
        super("Пользователь с id: "+ id + " уже создан");
    }
    public UserAlreadyExistsException(String email) {
        super("Пользователь с email: "+ email + " уже создан");
    }
}
