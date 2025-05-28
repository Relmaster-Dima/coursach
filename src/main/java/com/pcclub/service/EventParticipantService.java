// src/main/java/com/pcclub/service/EventParticipantService.java
package com.pcclub.service;

import com.pcclub.model.EventParticipant;
import com.pcclub.model.EventParticipantId;
import com.pcclub.model.Event;
import com.pcclub.model.User;
import com.pcclub.repository.EventParticipantRepository;
import com.pcclub.repository.EventRepository;
import com.pcclub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventParticipantService {
    @Autowired
    private EventParticipantRepository eventParticipantRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public boolean isRegistered(int userId, int eventId) {
        EventParticipantId id = new EventParticipantId(eventId, userId);
        return eventParticipantRepository.existsById(id);
    }
    public List<Event> getEventsForUser(int userId) {
        return eventParticipantRepository.findByIdUserId(userId).stream()
                .map(EventParticipant::getEvent)
                .collect(Collectors.toList());
    }

    public void register(int userId, int eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        EventParticipantId id = new EventParticipantId(eventId, userId);

        // Если уже зарегистрирован, не добавляем
        if (eventParticipantRepository.existsById(id)) {
            return;
        }

        EventParticipant participant = new EventParticipant();
        participant.setId(id);
        participant.setEvent(event);
        participant.setUser(user);
        participant.setRegisteredAt(java.time.LocalDateTime.now());

        eventParticipantRepository.save(participant);
    }
}

