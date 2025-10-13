package ru.yandex.practicum.filmorate.storage.event;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.FeedEvent;
import ru.yandex.practicum.filmorate.model.event.Operation;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(FeedEvent e) {
        final String sql = """
                INSERT INTO feed_events (ts, user_id, event_type, operation, entity_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, e.getTs());
            ps.setLong(2, e.getUserId());
            ps.setString(3, e.getEventType().name());
            ps.setString(4, e.getOperation().name());
            ps.setLong(5, e.getEntityId());
            return ps;
        }, kh);
        if (kh.getKey() != null) {
            e.setId(kh.getKey().longValue());
        }
    }

    @Override
    public List<FeedEvent> findByUser(long userId) {
        final String sql = """
                SELECT id, ts, user_id, event_type, operation, entity_id
                FROM feed_events
                WHERE user_id = ?
                ORDER BY ts ASC, id ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> map(rs), userId);
    }

    private FeedEvent map(ResultSet rs) throws SQLException {
        FeedEvent e = new FeedEvent();
        e.setId(rs.getLong("id"));
        e.setTs(rs.getLong("ts"));
        e.setUserId(rs.getLong("user_id"));
        e.setEventType(EventType.valueOf(rs.getString("event_type")));
        e.setOperation(Operation.valueOf(rs.getString("operation")));
        e.setEntityId(rs.getLong("entity_id"));
        return e;
    }
}