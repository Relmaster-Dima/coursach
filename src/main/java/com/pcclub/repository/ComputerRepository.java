package com.pcclub.repository;

import com.pcclub.model.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ComputerRepository extends JpaRepository<Computer, Integer> {
    List<Computer> findByIsAvailableTrue();
    @Query("""
      SELECT c FROM Computer c
      WHERE c.isAvailable = true
        AND c.id NOT IN (
           SELECT r.resourceId FROM Reservation r
           WHERE r.resourceType = com.pcclub.model.ResourceType.COMPUTER
             AND r.status <> com.pcclub.model.ReservationStatus.CANCELLED
             AND r.startTime < :end
             AND r.endTime > :start
        )
    """)
    List<Computer> findAvailableBetween(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );
}
