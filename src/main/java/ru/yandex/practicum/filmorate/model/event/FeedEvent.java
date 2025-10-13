package ru.yandex.practicum.filmorate.model.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedEvent {
    private Long id;
    private Long ts;        // epoch millis
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}