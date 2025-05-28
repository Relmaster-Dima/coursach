// src/main/java/com/pcclub/model/EventParticipant.java
package com.pcclub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants")
public class EventParticipant {

    @EmbeddedId
    private EventParticipantId id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "result")
    private String result;

    public EventParticipant() {
        this.registeredAt = LocalDateTime.now();
    }

    public EventParticipant(EventParticipantId id, Event event, User user) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.registeredAt = LocalDateTime.now();
    }

    public EventParticipantId getId() {
        return id;
    }

    public void setId(EventParticipantId id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
