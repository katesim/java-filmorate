package ru.com.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@SuperBuilder
public class Event {
    private Timestamp timestamp;
    private Long userId;
    private EventTypes eventType;
    private OperationTypes operation;
    private Long eventId;
    private Long entityId;
}
