package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("Должен быть положительным. Текущее значение:" + id, "id");
        }
        return userStorage.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User user) {
        user.setId(null);
        validate(user);
        String email = normalizeEmail(user.getEmail());
        user.setEmail(email);

        if (userStorage.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IncorrectParameterException("Не должен быть null", "id");
        }
        if (user.getId() <= 0) {
            throw new IncorrectParameterException("Должен быть положительным. Текущее значение:" + user.getId(), "id");
        }
        if (userStorage.getUser(user.getId()).isEmpty()) {
            throw new UserNotFoundException(user.getId());
        }
        validate(user);
        String newEmail = normalizeEmail(user.getEmail());
        user.setEmail(newEmail);

        userStorage.findByEmail(newEmail).ifPresent(other -> {
            if (!other.getId().equals(user.getId())) {
                throw new UserAlreadyExistsException(newEmail);
            }
        });

        return userStorage.updateUser(user);
    }

    public User addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectParameterException("нельзя добавить себя в друзья", "friendId");
        }
        User u = userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        User f = userStorage.getUser(friendId)
                .orElseThrow(() -> new UserNotFoundException(friendId));

        u.getFriends().add(friendId);
        f.getFriends().add(userId);

        userStorage.updateUser(u);
        userStorage.updateUser(f);
        return u;
    }

    public List<User> getFriends(long userId) {
        User u = userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return u.getFriends().stream().map(id -> userStorage.getUser(userId).orElse(null))
                .filter(Objects::nonNull).toList();
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User u = userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        User o = userStorage.getUser(otherId)
                .orElseThrow(() -> new UserNotFoundException(otherId));
        Set<Long> friends = new HashSet<>(u.getFriends());
        friends.retainAll(o.getFriends());
        return friends.stream().map(id -> userStorage.getUser(userId).orElse(null))
                .filter(Objects::nonNull).toList();
    }

    public User deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new IncorrectParameterException("нельзя удалить себя в друзья", "friendId");
        }
        User u = userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        User f = userStorage.getUser(friendId)
                .orElseThrow(() -> new UserNotFoundException(friendId));

        u.getFriends().remove(friendId);
        f.getFriends().remove(userId);

        userStorage.updateUser(u);
        userStorage.updateUser(f);
        return u;
    }


    private String normalizeEmail(String email) {
        if (email == null) {
            throw new IncorrectParameterException("Не должен быть null", "email");
        }
        String e = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (e.isEmpty()) {
            throw new IncorrectParameterException("Не должен быть пустым", "email");
        }
        return e;
    }

    private void validate(User u) {
        if (u.getLogin() != null && u.getLogin().contains(" ")) {
            throw new IncorrectParameterException("Не должен быть пустым", "login");
        }
        if (u.getName() == null || u.getName().isBlank()) {
            u.setName(u.getLogin());
        }
    }

    public List<Film> getRecommendFilms(long userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return filmStorage.getRecommendFilms(userId);
    }
}
