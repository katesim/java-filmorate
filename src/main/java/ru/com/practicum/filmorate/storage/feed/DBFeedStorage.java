package ru.com.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.com.practicum.filmorate.exception.NotFoundException;
import ru.com.practicum.filmorate.model.Event;
import ru.com.practicum.filmorate.model.EventTypes;
import ru.com.practicum.filmorate.model.OperationTypes;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DBFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBFeedStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getByUserId(Long userId) {
        String sqlQuery =
                "SELECT f.id, " +
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
        Long entityId = addEntityId(event.getEntityId(), event.getEventType());

        String sqlQuery = "INSERT INTO feed (created_at, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setTimestamp(1, event.getTimestamp());
            statement.setLong(2, event.getUserId());
            statement.setString(3, event.getEventType().toString());
            statement.setString(4, event.getOperation().toString());
            statement.setLong(5, entityId);
            return statement;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    private Long addEntityId(Long entityId, EventTypes eventType) {
        String sqlQuery = "INSERT INTO entities (" + getColumnName(eventType) + ") VALUES (?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setLong(1, entityId);
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long getEntityId(Long entityId, EventTypes eventType) {
        String column = getColumnName(eventType);
        String sqlQuery = "SELECT e." + column + " FROM entities AS e WHERE e.id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, entityId);

        if (!rowSet.next()) {
            throw new NotFoundException("Событие с id=" + entityId + " не существует");
        }

        return rowSet.getLong(column);
    }

    private String getColumnName(EventTypes eventType) {
        switch (eventType) {
            case FRIEND:
                return "friend_id";
            case LIKE:
                return "liked_film_id";
            case REVIEW:
                return "review_id";
            default:
                throw new NotFoundException("Для события " + eventType + " не существует столбца в таблице");
        }
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Timestamp timestamp = new Timestamp(rs.getTimestamp("created_at").getTime());
        Long userId = rs.getLong("user_id");
        EventTypes eventType = EventTypes.valueOf(rs.getString("event_type"));
        OperationTypes operation = OperationTypes.valueOf(rs.getString("operation"));
        Long eventId = rs.getLong("id");
        Long entityId = rs.getLong("entity_id");
        Long specificEntityId = getEntityId(entityId, eventType);

        return new Event(timestamp, userId, eventType, operation, eventId, specificEntityId);
    }

}
