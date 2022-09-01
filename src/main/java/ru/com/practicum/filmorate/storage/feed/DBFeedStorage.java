package ru.com.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.model.EventTypes;
import ru.com.practicum.filmorate.model.OperationTypes;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component
public class DBFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBFeedStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getByUserId(Long userId) {
        String sqlQuery = "SELECT f.id, " +
                                 "f.created_at, " +
                                 "f.user_id, " +
                                 "f.event_type, " +
                                 "f.operation, " +
                                 "f.entity_id " +
                          "FROM feed AS f " +
                          "WHERE f.user_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public Event addEvent(Event event) {
        String sqlQuery = "INSERT INTO feed (created_at, user_id, event_type, operation, entity_id) " +
                          "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setLong(1, event.getTimestamp());
            statement.setLong(2, event.getUserId());
            statement.setString(3, event.getEventType().toString());
            statement.setString(4, event.getOperation().toString());
            statement.setLong(5, event.getEntityId());
            return statement;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Long timestamp = rs.getLong("created_at");
        Long userId = rs.getLong("user_id");
        EventTypes eventType = EventTypes.valueOf(rs.getString("event_type"));
        OperationTypes operation = OperationTypes.valueOf(rs.getString("operation"));
        Long eventId = rs.getLong("id");
        Long entityId = rs.getLong("entity_id");

        return new Event(timestamp, userId, eventType, operation, eventId, entityId);
    }

}
