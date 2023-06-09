package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Long timestamp;
    Long userId;
    EventTypes eventType;
    Operations operation;
    Long eventId;
    Long entityId;
}
