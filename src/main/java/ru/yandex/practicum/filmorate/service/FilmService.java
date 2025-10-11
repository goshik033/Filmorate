package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, FilmDbStorage filmDbStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.filmDbStorage = filmDbStorage;
    }

    public Film addLike(long filmId, long userId) {

        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Film film = filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));


        return filmDbStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException(filmId));

        filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count);
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

}
