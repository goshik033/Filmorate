package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    @Override
    public List<Film> getAllFilms() {
        final String sql = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration_minutes,
                       m.id AS mpa_id, m.name AS mpa_name
                FROM films f
                JOIN mpa_rating m ON m.id = f.mpa_rating_id
                """;

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film f : films) {
            f.setGenres(getGenresByFilmId(f.getId()));
        }
        return films;
    }


    @Override
    public Optional<Film> getFilm(long id) {
        final String sql = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration_minutes,
                       m.id AS mpa_id, m.name AS mpa_name
                FROM films f
                JOIN mpa_rating m ON m.id = f.mpa_rating_id
                WHERE f.id = ?
                """;
        List<Film> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (list.isEmpty()) return Optional.empty();

        Film film = list.get(0);
        film.setGenres(getGenresByFilmId(film.getId()));
        return Optional.of(film);
    }


    @Override
    public Film addFilm(Film film) {
        final String sql = """
                INSERT INTO films (name, description, release_date, duration_minutes, mpa_rating_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription() == null ? "" : film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDurationMinutes());
            ps.setLong(5, film.getMpaRating().getId());
            return ps;
        }, kh);

        film.setId(kh.getKey().longValue());

        replaceGenres(film.getId(), film.getGenres());

        film.setGenres(getGenresByFilmId(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = """
                UPDATE films
                   SET name = ?,
                       description = ?,
                       release_date = ?,
                       duration_minutes = ?,
                       mpa_rating_id = ?
                 WHERE id = ?
                """;

        int rows = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription() == null ? "" : film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDurationMinutes());
            ps.setLong(5, film.getMpaRating().getId());
            ps.setLong(6, film.getId());
            return ps;
        });

        if (rows == 0) {
            throw new FilmNotFoundException(film.getId());
        }

        replaceGenres(film.getId(), film.getGenres());

        final String select = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration_minutes,
                       m.id AS mpa_id, m.name AS mpa_name
                  FROM films f
                  JOIN mpa_rating m ON m.id = f.mpa_rating_id
                 WHERE f.id = ?
                """;
        Film updated = jdbcTemplate.queryForObject(select, (rs, rowNum) -> makeFilm(rs), film.getId());
        updated.setGenres(getGenresByFilmId(updated.getId()));
        return updated;
    }

    @Override
    public Film addLike(long filmId, long userId) {
        final String sql = """
                INSERT INTO user_likes_film (film_id, user_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM user_likes_film WHERE film_id = ? AND user_id = ?
                )
                """;
        jdbcTemplate.update(sql, filmId, userId, filmId, userId);
        return getFilm(filmId).get();
    }

    @Override
    public void removeLike(long filmId, long userId) {
        final String sql = """
                DELETE FROM user_likes_film 
                WHERE film_id =? AND user_id =?
                """;
        jdbcTemplate.update(sql, filmId, userId);

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) return List.of();

        final String sql = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration_minutes,
                    m.id  AS mpa_id,
                    m.name AS mpa_name,
                    COUNT(ulf.user_id) AS like_count
                FROM films f
                LEFT JOIN user_likes_film ulf ON ulf.film_id = f.id
                JOIN mpa_rating m ON m.id = f.mpa_rating_id
                GROUP BY
                    f.id, f.name, f.description, f.release_date, f.duration_minutes,
                    m.id, m.name
                ORDER BY like_count DESC, f.id ASC
                LIMIT ?
                """;

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);

        for (Film f : films) {
            f.setGenres(getGenresByFilmId(f.getId()));
        }
        return films;
    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getObject("release_date", LocalDate.class);
        long durationMinutes = rs.getLong("duration_minutes");
        long mpaId = rs.getLong("mpa_id");
        String mpaName = rs.getString("mpa_name");
        MpaRating mpaRating = new MpaRating(mpaId, mpaName);
        return new Film(id, name, description, releaseDate, durationMinutes, mpaRating, new LinkedHashSet<>());
    }

    private void replaceGenres(long filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", filmId);
        if (genres == null || genres.isEmpty()) return;

        var ids = genres.stream()
                .map(Genre::getId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(java.sql.PreparedStatement ps, int i) throws java.sql.SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, ids.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return ids.size();
                    }
                }
        );
    }

    private java.util.LinkedHashSet<Genre> getGenresByFilmId(long filmId) {
        final String sql = """
                SELECT g.id, g.name
                  FROM film_genre fg
                  JOIN genres g ON g.id = fg.genre_id
                 WHERE fg.film_id = ?
                 ORDER BY g.id
                """;
        var list = jdbcTemplate.query(sql, (rs, rn) ->
                new Genre(rs.getLong("id"), rs.getString("name")), filmId);
        return new java.util.LinkedHashSet<>(list);
    }
}
