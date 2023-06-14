package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface FeedStorage {

    Collection<Event> getFeedById(int userId);

    Event addEvent(int userId, EventTypes eventType, Operations operation, int entityId);
}
