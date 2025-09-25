package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exeption.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
}
