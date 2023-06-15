package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventTypes;
import ru.yandex.practicum.filmorate.enums.Operations;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;

import java.sql.PreparedStatement;
import java.util.Collection;

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
    public Collection<Event> getFeedById(int userId) {
        final String sql = "SELECT * FROM feed WHERE user_id = ?";
        log.info("Лента событий пользователя с индентификатором {} отправлена", userId);
        return jdbcTemplate.query(sql, eventMapper, userId);
    }

    @Override
    public Event addEvent(int userId, EventTypes eventType, Operations operation, int entityId) {
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .eventId(0)
                .build();

        final String sql = "INSERT INTO feed (timestamps, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setLong(1, event.getTimestamp());
            stmt.setInt(2, event.getUserId());
            stmt.setString(3, event.getEventType().toString());
            stmt.setString(4, event.getOperation().toString());
            stmt.setInt(5, event.getEntityId());
            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            event.setEventId((Integer) keyHolder.getKey());
        }
        log.info("Создано событие с индентификатором {} ", event.getEventId());
        return event;
    }
}
