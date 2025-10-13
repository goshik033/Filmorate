package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.event.FeedEvent;

import java.util.List;

public interface EventStorage {
    void add(FeedEvent e);
     List<FeedEvent> findByUser(long userId);
}
