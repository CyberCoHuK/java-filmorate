package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Primary
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate, EventMapper eventMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventMapper = eventMapper;
    }

    @Override
    public Collection<Event> getFeedById(Long userId) {
        final String sql = "SELECT * FROM feed WHERE user_id = ?";
        log.info("Лента событий пользователя с индентификатором {} отправлена", userId);
        return jdbcTemplate.query(sql, eventMapper, userId);
    }

    @Override
    public Event addEvent(Long userId, EventTypes eventType, Operations operation, Long entityId) {
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .eventId(0L)
                .build();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = jdbcInsert.withTableName("feed")
                .usingGeneratedKeyColumns("event_id")
                .executeAndReturnKey(getEventFields(event));
        event.setEventId(key.longValue());
        log.info("Создано событие с индентификатором {} ", event.getEventId());
        return event;
    }

    private Map<String, Object> getEventFields(Event event) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("TIMESTAMPS", event.getTimestamp());
        fields.put("USER_ID", event.getUserId());
        fields.put("EVENT_TYPE", event.getEventType());
        fields.put("OPERATION", event.getOperation());
        fields.put("ENTITY_ID", event.getEntityId());
        return fields;
    }
}
