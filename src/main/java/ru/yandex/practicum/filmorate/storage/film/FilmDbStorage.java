package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
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
            f.setGenres(genreStorage.getGenresByFilmId(f.getId()));
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
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
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
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, kh);

        film.setId(kh.getKey().longValue());

        genreStorage.replaceGenres(film.getId(), film.getGenres());

        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
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
            ps.setLong(5, film.getMpa().getId());
            ps.setLong(6, film.getId());
            return ps;
        });

        if (rows == 0) {
            throw new FilmNotFoundException(film.getId());
        }

        genreStorage.replaceGenres(film.getId(), film.getGenres());

        final String select = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration_minutes,
                       m.id AS mpa_id, m.name AS mpa_name
                  FROM films f
                  JOIN mpa_rating m ON m.id = f.mpa_rating_id
                 WHERE f.id = ?
                """;
        Film updated = jdbcTemplate.queryForObject(select, (rs, rowNum) -> makeFilm(rs), film.getId());
        updated.setGenres(genreStorage.getGenresByFilmId(updated.getId()));
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
            f.setGenres(genreStorage.getGenresByFilmId(f.getId()));
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
        Mpa mpa = new Mpa(mpaId, mpaName);
        return new Film(id, name, description, releaseDate, durationMinutes, mpa, new LinkedHashSet<>());
    }


}
