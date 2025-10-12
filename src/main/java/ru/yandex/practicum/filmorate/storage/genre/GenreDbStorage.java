package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void replaceGenres(long filmId, Set<Genre> genres) {
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

    @Override
    public java.util.LinkedHashSet<Genre> getGenresByFilmId(long filmId) {
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

    @Override
    public Optional<Genre> getGenre(long id) {
        final String sql = """
                SELECT g.id, g.name 
                FROM genres g
                WHERE g.id = ?""";
        try {
            Genre g = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public List<Genre> getAllGenres() {
        final String sql = """
                SELECT g.id, g.name 
                FROM genres g
                """;
        return jdbcTemplate.query(sql, (rs, rowNet) -> makeGenre(rs));
    }


    private Genre makeGenre(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
