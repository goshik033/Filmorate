package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.FeedEvent;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public List<FeedEvent> getFeedByUser(long userId) {
        return eventStorage.findByUser(userId);
    }

    public void publish(long userId, EventType type, Operation op, long entityId) {
        FeedEvent e = new FeedEvent();
        e.setTs(Instant.now().toEpochMilli());
        e.setUserId(userId);
        e.setEventType(type);
        e.setOperation(op);
        e.setEntityId(entityId);
        eventStorage.add(e);
    }
}