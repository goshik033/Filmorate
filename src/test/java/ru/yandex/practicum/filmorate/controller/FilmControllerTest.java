package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    void getAllFilms_empty_returnsEmptyList() {
        FilmController c = new FilmController();
        List<Film> all = c.getAllFilms();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void addFilm_valid_assignsIdAndStores() {
        FilmController c = new FilmController();
        Film f = film("Matrix", "Neo", LocalDate.of(1999, 3, 31), Duration.ofMinutes(136));

        Film saved = c.addFilm(f);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals("Matrix", c.getFilm(saved.getId()).getName());
    }

    @Test
    void addFilm_releaseBeforeCinemaBirthday_throws400() {
        FilmController c = new FilmController();
        Film f = film("Old", "History", LocalDate.of(1800, 1, 1), Duration.ofMinutes(10));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.addFilm(f));
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Дата релиза"));
    }

    @Test
    void getFilm_notFound_throws404() {
        FilmController c = new FilmController();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.getFilm(999L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void getFilm_badId_throws400() {
        FilmController c = new FilmController();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.getFilm(0L));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void updateFilm_notFound_throws404() {
        FilmController c = new FilmController();
        Film f = film("X", "Y", LocalDate.of(2000, 1, 1), Duration.ofMinutes(1));
        f.setId(777L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.updateFilm(f));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateFilm_badId_throws400() {
        FilmController c = new FilmController();
        Film f = film("X", "Y", LocalDate.of(2000, 1, 1), Duration.ofMinutes(1));

        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () -> c.updateFilm(f));
        assertEquals(400, ex1.getStatusCode().value());

        f.setId(0L);
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () -> c.updateFilm(f));
        assertEquals(400, ex2.getStatusCode().value());
    }

    @Test
    void updateFilm_valid_updates() {
        FilmController c = new FilmController();
        Film f = film("Initial", "D1", LocalDate.of(2001, 1, 1), Duration.ofMinutes(90));
        Film saved = c.addFilm(f);

        saved.setName("Updated");
        saved.setDescription("D2");
        Film updated = c.updateFilm(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("D2", updated.getDescription());
    }

    private static Film film(String name, String desc, LocalDate date, Duration dur) {
        Film f = new Film();
        f.setName(name);
        f.setDescription(desc);
        f.setReleaseDate(date);
        f.setDuration(dur);
        return f;
    }
}
