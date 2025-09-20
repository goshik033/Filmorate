package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> result = new ArrayList<>(films.values());
        log.info("GET /films -> {} films", result.size());
        log.debug("GET /films -> payload: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("GET /films/{} called", id);
        if (id == null || id <= 0) {
            log.warn("GET /films/{} -> bad id (null or <=0)", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        Film film = films.get(id);
        if (film == null) {
            log.warn("GET /films/{} -> not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден: id=" + id);
        }
        log.debug("GET /films/{} -> {}", id, film);
        return film;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films -> attempt create");
        validateReleaseDate(film);
        long id = seq.incrementAndGet();
        film.setId(id);
        films.put(id, film);
        log.info("POST /films -> created id={}", id);
        log.debug("POST /films -> payload: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Long id = film.getId();
        log.info("PUT /films -> attempt update id={}", id);

        if (id == null || id <= 0) {
            log.warn("PUT /films -> bad id: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный id: " + id);
        }
        if (!films.containsKey(id)) {
            log.warn("PUT /films -> not found id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден: id=" + id);
        }
        validateReleaseDate(film); // если хочешь контролировать регресс по дате при апдейте
        films.put(id, film);
        log.info("PUT /films -> updated id={}", id);
        log.debug("PUT /films -> payload: {}", film);
        return film;
    }

    private void validateReleaseDate(Film film) {
        LocalDate rd = film.getReleaseDate();
        if (rd == null || rd.isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Validation failed: releaseDate={} (must be >= {})", rd, CINEMA_BIRTHDAY);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата релиза не может быть раньше " + CINEMA_BIRTHDAY
            );
        }
    }


}
