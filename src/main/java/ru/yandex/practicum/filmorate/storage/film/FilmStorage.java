package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

}
