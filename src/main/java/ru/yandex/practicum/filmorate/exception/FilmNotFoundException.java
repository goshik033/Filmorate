package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(Long id) {
        super("Фильм с id: " + id + " не найден");
    }
}
