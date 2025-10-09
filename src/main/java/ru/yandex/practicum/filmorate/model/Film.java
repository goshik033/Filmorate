package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.stereotype.Indexed;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @Past
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Long durationMinutes;
    private Set<Long> likes = new HashSet<>();
    @NotEmpty
    private Set<Genre> genres = new HashSet<>();
    @NotNull
    private MpaRating mpaRating;

}
