package com.pcclub.service;

import com.pcclub.model.Event;
import com.pcclub.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAll().stream()
                .filter(event -> !event.getStartDate().isBefore(today))
                .toList();
    }

    // Получить все события
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Получить событие по id
    public Event getEventById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено с id: " + id));
    }

    // Добавить новое событие
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    // Удалить событие по id
    public void deleteEventById(int id) {
        eventRepository.deleteById(id);
    }

    // Обновить существующее событие
    public Event updateEvent(Event event) {
        if (!eventRepository.existsById(event.getId())) {
            throw new NoSuchElementException("Событие не найдено с id: " + event.getId());
        }
        return eventRepository.save(event);
    }
}
