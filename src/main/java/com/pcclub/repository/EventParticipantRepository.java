// src/main/java/com/pcclub/repository/EventParticipantRepository.java
package com.pcclub.repository;

import com.pcclub.model.EventParticipant;
import com.pcclub.model.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    boolean existsByUserIdAndEventId(int userId, int eventId);
    List<EventParticipant> findByIdUserId(int userId);
}

