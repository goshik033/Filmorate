package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpa(long id) {
        final String sql = """
                SELECT id,name
                FROM mpa_rating 
                WHERE id = ?""";
        try {
            Mpa m = jdbcTemplate.queryForObject(sql, this::makeMpa, id);
            return Optional.ofNullable(m);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Mpa> getAllMpa() {
        final String sql = """
                SELECT id,name
                FROM mpa_rating 
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs,rowNum));
    }

    private Mpa makeMpa(ResultSet rs,int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
