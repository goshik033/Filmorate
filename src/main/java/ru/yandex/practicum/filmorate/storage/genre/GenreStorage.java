package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    void replaceGenres(long filmId, Set<Genre> genres);

    LinkedHashSet<Genre> getGenresByFilmId(long filmId);

    List<Genre> getAllGenres();

    Optional<Genre> getGenre(long id);
}
