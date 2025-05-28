package com.pcclub.repository;

import com.pcclub.model.Reservation;
import com.pcclub.model.ResourceType;
import com.pcclub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("""
        SELECT r.resourceId
        FROM Reservation r
        WHERE r.resourceType = :type
          AND r.status <> com.pcclub.model.ReservationStatus.CANCELLED
          AND r.startTime < :end
          AND r.endTime > :start
    """)
    List<Integer> findBookedResourceIds(
            @Param("type") ResourceType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    List<Reservation> findByUserId(int userId);
    List<Reservation> findByUser(User user);
}
