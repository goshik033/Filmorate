package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getUsers();

    Optional<User> getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    Optional<User> findByEmail(String email);


}
