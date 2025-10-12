package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres = new java.util.LinkedHashSet<>();


}
