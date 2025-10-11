package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getUsers() {
        final String sql = "SELECT id,email,login,name,birthday FROM users";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        for (User user : users) {
            user.setFriends(getFriendIdsByUserId(user.getId()));
        }
        return users;
    }

    @Override
    public Optional<User> getUser(long id) {

        final String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
            user.setFriends(getFriendIdsByUserId(user.getId()));
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public User createUser(User user) {
        final String sql = """
                INSERT INTO users (email,login,name,birthday)
                VALUES (?,?,?,?)
                """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, kh);
        user.setId(kh.getKey().longValue());

        replaceFriends(user.getId(), user.getFriends());

        user.setFriends(getFriendIdsByUserId(user.getId()));

        return user;
    }

    @Override
    public User updateUser(User user) {
        final String sql = """
                UPDATE users SET email=?, login=?, name=?, birthday=?
                 WHERE id = ?
                """;

        int rows = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, user.getId());
            return ps;
        });

        if (rows == 0) {
            throw new UserNotFoundException(user.getId());
        }

        replaceFriends(user.getId(), user.getFriends());

        final String select = """
                SELECT u.id, u.name, u.login, u.email, u.birthday
                FROM users u
                WHERE u.id = ?
                """;
        User updated = jdbcTemplate.queryForObject(select, (rs, rowNum) -> makeUser(rs), user.getId());
        updated.setFriends(getFriendIdsByUserId(updated.getId()));
        return updated;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        final String sql = "SELECT id, email, login, name, birthday FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), email);
            user.setFriends(getFriendIdsByUserId(user.getId()));
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    private User makeUser(ResultSet rs) throws SQLException {

        long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getObject("birthday", LocalDate.class);
        return new User(id, email, login, name, birthday, new LinkedHashSet<>());
    }

    private void replaceFriends(long userId, Set<Long> friends) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id=?", userId);
        if (friends == null || friends.isEmpty()) return;
        List<Long> ids = new ArrayList<>(friends);
        jdbcTemplate.batchUpdate(
                "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, ids.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return ids.size();
                    }
                }
        );
    }

    public LinkedHashSet<Long> getFriendIdsByUserId(long userId) {
        final String sql = """
                SELECT f.friend_id
                FROM friends f
                WHERE f.user_id = ?
                ORDER BY f.friend_id
                """;
        var ids = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new LinkedHashSet<>(ids);
    }


}
