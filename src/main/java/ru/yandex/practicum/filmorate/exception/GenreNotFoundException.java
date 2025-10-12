package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(long id) {
        super("Жанр с id: " + id + " не найден");
    }


}
