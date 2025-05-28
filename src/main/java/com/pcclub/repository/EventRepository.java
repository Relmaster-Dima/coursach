// src/main/java/com/pcclub/repository/EventRepository.java
package com.pcclub.repository;

import com.pcclub.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    // все активные (ближайшие) события
    List<Event> findByEndDateGreaterThanEqualOrderByStartDate(LocalDate date);
}
