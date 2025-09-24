package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller;
    UserStorage userStorage;
    UserService userService;


    @BeforeEach
    protected void init() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        controller = new UserController(userService);
    }

    @Test
    void getUsers_empty_returnsEmptyList() {
        List<User> all = controller.getUsers();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void createUser_valid_setsId() {
        User u =  user(null, "neo", "Neo", "neo@zion.io");

        User saved = controller.createUser(u);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("neo", controller.getUser(saved.getId()).getLogin());
    }

    @Test
    void createUser_blankName_fallbacksToLogin() {

        User u = user(null, "trinity", "","trinity@zion.io");

        User saved = controller.createUser(u);
        assertEquals("trinity", saved.getName());
    }

    @Test
    void createUser_loginWithSpaces_throws400() {
        User u = user(null, "bad login", "Name",null);
        assertThrows(IncorrectParameterException.class, () -> controller.createUser(u));

    }

    @Test
    void getUser_badId_throws400() {
        assertThrows(IncorrectParameterException.class, () -> controller.getUser(-1));
    }

    @Test
    void getUser_notFound_throws404() {
        assertThrows(UserNotFoundException.class, () -> controller.getUser(999));
    }

    @Test
    void updateUser_badId_throws400() {

        User u = user(null, "smith", "Agent", "smith@zion.io");
        assertThrows(IncorrectParameterException.class, () -> controller.updateUser(u));

        u.setId(0L);
        assertThrows(IncorrectParameterException.class, () -> controller.updateUser(u));

    }

    @Test
    void updateUser_notFound_throws404() {

        User u = user(123L, "smith", "Agent", "smith@zion.io");
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(u));

    }

    @Test
    void updateUser_valid_updates() {

        User u = user(null, "morpheus", "Morpheus","morpheus@zion.io");
        User created = controller.createUser(u);

        created.setName("Captain");
        User updated = controller.updateUser(created);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Captain", updated.getName());
    }

    private static User user(Long id, String login, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setLogin(login);
        u.setName(name);
        u.setEmail(email);
        return u;
    }
}
