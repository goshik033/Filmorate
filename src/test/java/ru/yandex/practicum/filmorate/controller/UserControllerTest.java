package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void getUsers_empty_returnsEmptyList() {
        UserController c = new UserController();
        List<User> all = c.getUsers();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void createUser_valid_setsId() {
        UserController c = new UserController();
        User u = user(null, "neo", "Neo");

        User saved = c.createUser(u);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("neo", c.getUser(saved.getId()).getLogin());
    }

    @Test
    void createUser_blankName_fallbacksToLogin() {
        UserController c = new UserController();
        User u = user(null, "trinity", "");

        User saved = c.createUser(u);
        assertEquals("trinity", saved.getName());
    }

    @Test
    void createUser_loginWithSpaces_throws400() {
        UserController c = new UserController();
        User u = user(null, "bad login", "Name");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.createUser(u));
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("login"));
    }

    @Test
    void getUser_badId_throws400() {
        UserController c = new UserController();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.getUser(-1));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void getUser_notFound_throws404() {
        UserController c = new UserController();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.getUser(999));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateUser_badId_throws400() {
        UserController c = new UserController();
        User u = user(null, "smith", "Agent");
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () -> c.updateUser(u));
        assertEquals(400, ex1.getStatusCode().value());

        u.setId(0L);
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () -> c.updateUser(u));
        assertEquals(400, ex2.getStatusCode().value());
    }

    @Test
    void updateUser_notFound_throws404() {
        UserController c = new UserController();
        User u = user(123L, "smith", "Agent");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.updateUser(u));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateUser_valid_updates() {
        UserController c = new UserController();
        User u = user(null, "morpheus", "Morpheus");
        User created = c.createUser(u);

        created.setName("Captain");
        User updated = c.updateUser(created);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Captain", updated.getName());
    }

    private static User user(Long id, String login, String name) {
        User u = new User();
        u.setId(id);
        u.setLogin(login);
        u.setName(name);
        return u;
    }
}
