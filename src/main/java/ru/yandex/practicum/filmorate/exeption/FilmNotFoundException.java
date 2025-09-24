package ru.yandex.practicum.filmorate.exeption;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(Long id) {
        super("Фильм с id: " + id + " не найден");
    }
}
