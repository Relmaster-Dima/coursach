package com.pcclub.repository;

import com.pcclub.model.Console;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsoleRepository extends JpaRepository<Console, Integer> {

    // Метод для получения всех консолей, помеченных как доступные
    List<Console> findByIsAvailableTrue();

    // Метод для фильтрации по доступности в заданном интервале
    @Query("""
      SELECT c FROM Console c
      WHERE c.isAvailable = true
        AND c.id NOT IN (
           SELECT r.resourceId FROM Reservation r
           WHERE r.resourceType = com.pcclub.model.ResourceType.CONSOLE
             AND r.status <> com.pcclub.model.ReservationStatus.CANCELLED
             AND r.startTime < :end
             AND r.endTime > :start
        )
    """)
    List<Console> findAvailableBetween(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );
}
