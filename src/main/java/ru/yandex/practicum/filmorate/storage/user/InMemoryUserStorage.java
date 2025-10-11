package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStorage implements UserStorage {


    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }



    @Override
    public User createUser(User user) {
        long id = seq.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        users.put(id, user);
        return user;

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }

}
