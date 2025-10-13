package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film addFilm(Film film) {
        long id = seq.incrementAndGet();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film addLike(long filmId, long userId) {
        return null;
    }

    @Override
    public void removeLike(long filmId, long userId) {

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return List.of();
    }

    @Override
    public List<Film> searchFilm(String query, Set<SearchBy> by, Integer limit, Integer offset) {
        return List.of();
    }


}



