package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmStorage filmStorage;
    FilmService filmService;
    FilmController controller;
    UserStorage userStorage;

    @BeforeEach
    protected void init() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        controller = new FilmController(filmService);
    }

    @Test
    void getAllFilms_empty_returnsEmptyList() {
        List<Film> all = controller.getAllFilms();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void addFilm_valid_assignsIdAndStores() {

        Film f = film("Matrix", "Neo", LocalDate.of(1999, 3, 31), 136);

        Film saved = controller.addFilm(f);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("Matrix", controller.getFilm(saved.getId()).getName());
    }

    @Test
    void addFilm_releaseBeforeCinemaBirthday_throws400() {

        Film f = film("Old", "History", LocalDate.of(1800, 1, 1), 10);

        assertThrows(IncorrectParameterException.class, () -> controller.addFilm(f));

    }

    @Test
    void getFilm_notFound_throws404() {

        assertThrows(FilmNotFoundException.class, () -> controller.getFilm(999L));

    }

    @Test
    void getFilm_badId_throws400() {

        assertThrows(IncorrectParameterException.class, () -> controller.getFilm(0L));

    }

    @Test
    void updateFilm_notFound_throws404() {

        Film f = film("X", "Y", LocalDate.of(2000, 1, 1), 1);
        f.setId(777L);

        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(f));

    }

    @Test
    void updateFilm_badId_throws400() {

        Film f = film("X", "Y", LocalDate.of(2000, 1, 1), 1);

        assertThrows(IncorrectParameterException.class, () -> controller.updateFilm(f));


        f.setId(0L);
        assertThrows(IncorrectParameterException.class, () -> controller.updateFilm(f));

    }

    @Test
    void updateFilm_valid_updates() {

        Film f = film("Initial", "D1", LocalDate.of(2001, 1, 1), 90);
        Film saved = controller.addFilm(f);

        saved.setName("Updated");
        saved.setDescription("D2");
        Film updated = controller.updateFilm(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("D2", updated.getDescription());
    }

    private static Film film(String name, String desc, LocalDate date, long dur) {
        Film f = new Film();
        f.setName(name);
        f.setDescription(desc);
        f.setReleaseDate(date);
        f.setDurationMinutes(dur);
        return f;
    }
}
