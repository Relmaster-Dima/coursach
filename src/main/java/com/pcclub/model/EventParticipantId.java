// src/main/java/com/pcclub/model/EventParticipantId.java
package com.pcclub.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventParticipantId implements Serializable {
    private Integer eventId;
    private Integer userId;

    public EventParticipantId() {}

    public EventParticipantId(Integer eventId, Integer userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventParticipantId that = (EventParticipantId) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId);
    }
}
