package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(long id) {
        super("Mpa с id: " + id + " не найден");
    }
}
