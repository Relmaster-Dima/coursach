package com.pcclub.controller;

import com.pcclub.model.ReservationStatus;
import com.pcclub.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private JdbcTemplate jdbc;

    // 1) Создание новой брони со статусом PENDING
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String,String> p, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String type = p.get("resourceType");
        int    id   = Integer.parseInt(p.get("resourceId"));
        LocalDateTime start = LocalDateTime.parse(p.get("startTime"));
        LocalDateTime end   = LocalDateTime.parse(p.get("endTime"));

        jdbc.update(
                "INSERT INTO reservations(user_id, resource_type, resource_id, start_time, end_time, status) " +
                        "VALUES(?,?,?,?,?,?)",
                user.getId(), type, id, start, end, ReservationStatus.PENDING.name()
        );
        return ResponseEntity.ok(Map.of("status","PENDING"));
    }

    // 2) Получение списка «PENDING»-бронирований для текущего пользователя
    @GetMapping("/pending")
    public List<Map<String,Object>> pending(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return List.of();

        return jdbc.queryForList(
                "SELECT r.id, r.resource_type, r.resource_id, r.start_time, r.end_time, r.status, " +
                        "       COALESCE(c.name, co.name) AS resourceName, " +
                        "       COALESCE(c.price, co.price) AS resourcePrice " +
                        "  FROM reservations r " +
                        "  LEFT JOIN computers c ON r.resource_type='COMPUTER' AND r.resource_id=c.id " +
                        "  LEFT JOIN consoles  co ON r.resource_type='CONSOLE'  AND r.resource_id=co.id " +
                        " WHERE r.user_id=? AND r.status='PENDING'",
                user.getId()
        );
    }
}
