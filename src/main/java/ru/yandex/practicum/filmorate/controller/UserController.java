package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        List<User> result = new ArrayList<>(users.values());
        log.info("GET /users -> {} users", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("GET /users/{} called", id);
        if (id < 0) {
            log.warn("GET /users/{} -> bad id (<0)", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        User user = users.get(id);
        if (user == null) {
            log.warn("GET /users/{} -> not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User не найден id=" + id);
        }
        log.debug("GET /users/{} -> {}", id, user);
        return user;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        long id = seq.incrementAndGet();
        user.setId(id);
        log.info("POST /users -> attempt create id={}", id);
        validate(user);
        users.put(id, user);
        log.info("POST /users -> created id={}", id);
        log.debug("POST /users -> payload: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long id = user.getId();
        log.info("PUT /users -> attempt update id={}", id);

        if (id == null || id <= 0) {
            log.warn("PUT /users -> bad id: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        if (!users.containsKey(id)) {
            log.warn("PUT /users -> not found id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User не найден: id=" + id);
        }
        validate(user);
        users.put(id, user);
        log.info("PUT /users -> updated id={}", id);
        log.debug("PUT /users -> payload: {}", user);
        return user;

    }

    private void validate(User u) {
        if (u.getLogin() != null && u.getLogin().contains(" ")) {
            log.warn("Validation failed: login contains spaces (login='{}')", u.getLogin());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "login не должен содержать пробелы");
        }
        if (u.getName() == null || u.getName().isBlank()) {
            log.debug("Validation: name is blank, fallback to login ('{}')", u.getLogin());
            u.setName(u.getLogin());
        }
    }
}
