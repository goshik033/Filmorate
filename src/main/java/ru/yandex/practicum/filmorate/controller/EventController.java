package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.event.FeedEvent;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/feed")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<FeedEvent> getFeed(@PathVariable long userId) {
        return eventService.getFeedByUser(userId);
    }
}