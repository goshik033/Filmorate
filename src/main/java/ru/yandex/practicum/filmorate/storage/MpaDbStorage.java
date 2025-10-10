package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private static JdbcTemplate jdbcTemplate;


    public MpaRating getMpa(long mpaId) {
        String sql = "SELECT name FROM mpa_type WHERE id=?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), mpaId);
    }

    private MpaRating makeMpa(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        String name = rs.getString("name");
        return new MpaRating(id, name);
    }
}
