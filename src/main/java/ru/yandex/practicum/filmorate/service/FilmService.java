package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
