package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;

    }

    public Film addLike(long filmId, long userId) {

        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));


        return filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }


    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("id должен быть положительным. Текущее значение: " + id, "filmId");
        }
        return filmStorage.getFilm(id).orElseThrow(() -> new FilmNotFoundException(id));
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new IncorrectParameterException("Не должен быть null", "filmId");
        }
        if (film.getId() <= 0) {
            throw new IncorrectParameterException("filmId должен быть положительным. Текущее значение: " + film.getId(), "filmId");
        }
        if (filmStorage.getFilm(film.getId()).isEmpty()) {
            throw new FilmNotFoundException(film.getId());
        }
        validateReleaseDate(film);
        filmStorage.updateFilm(film);

        return film;
    }

    private void validateReleaseDate(Film film) {
        LocalDate rd = film.getReleaseDate();
        if (rd == null || rd.isBefore(CINEMA_BIRTHDAY)) {
            throw new IncorrectParameterException(
                    "Дата релиза не может быть раньше " + CINEMA_BIRTHDAY, "releaseDate");
        }
    }


    public List<Film> searchFilms(String query,
                                  Set<FilmStorage.SearchBy> by,
                                  Integer limit,
                                  Integer offset) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        if (by == null || by.isEmpty()) {
            by = EnumSet.of(FilmStorage.SearchBy.TITLE);
        }
        return filmStorage.searchFilm(query.trim(), by, limit, offset);
    }

}
