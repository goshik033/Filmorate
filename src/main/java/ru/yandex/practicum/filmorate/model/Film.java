package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @Past
    @NotNull
    private LocalDate releaseDate;
    @Positive
    @NotNull
    private Duration duration;
    private Set<Integer> likes;
}
