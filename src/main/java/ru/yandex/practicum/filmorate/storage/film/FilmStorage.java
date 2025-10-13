package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    List<Film> getAllFilms();

    Optional<Film> getFilm(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);

    List<Film> searchFilm(String query, Set<SearchBy> by, Integer limit, Integer offset);

    enum SearchBy {
        TITLE, DIRECTOR, TITLE_AND_DIRECTOR
    }
}


