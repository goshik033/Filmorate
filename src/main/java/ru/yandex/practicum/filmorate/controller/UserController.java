package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        if (id < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        User user = users.get(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User не найден id=" + id);
        }
        return user;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        long id = seq.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long id = user.getId();

        if (id== null || id <= 0  ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User не найден: id=" + id);
        }
        users.put(id, user);
        return user;

    }
}
