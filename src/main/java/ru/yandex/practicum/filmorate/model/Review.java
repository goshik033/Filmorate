package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Review {
    private long id;
    private long filmId;
    private long userId;
    private String content;
    private boolean isPositive;
    private int useful;
}
