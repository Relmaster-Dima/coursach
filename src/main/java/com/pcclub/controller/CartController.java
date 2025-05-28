package com.pcclub.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private JdbcTemplate jdbc;

    // Показ корзины
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Integer.class,
                username
        );
        if (userId == null) {
            return "redirect:/login";
        }

        // Получаем все PENDING-брони
        List<Map<String,Object>> list = jdbc.queryForList(
                "SELECT r.id,\n" +
                        "       r.resource_type,\n" +
                        "       r.start_time,\n" +
                        "       r.end_time,\n" +
                        "       r.status,\n" +
                        "       COALESCE(c.name, co.name)   AS resourceName,\n" +
                        "       COALESCE(c.price, co.price) AS resourcePrice\n" +
                        "  FROM reservations r\n" +
                        "  LEFT JOIN computers c ON r.resource_type='COMPUTER' AND r.resource_id=c.id\n" +
                        "  LEFT JOIN consoles  co ON r.resource_type='CONSOLE'  AND r.resource_id=co.id\n" +
                        " WHERE r.user_id = ?\n" +
                        "   AND r.status  = 'PENDING'",
                userId
        );

        // Считаем длительность и стоимость каждой брони
        for (Map<String,Object> r : list) {
            LocalDateTime st = (LocalDateTime) r.get("start_time");
            LocalDateTime en = (LocalDateTime) r.get("end_time");
            long hours = Duration.between(st, en).toHours();
            double price = ((Number) r.get("resourcePrice")).doubleValue();
            r.put("hours", hours);
            r.put("cost", price * hours);
        }

        double total = list.stream()
                .mapToDouble(r -> ((Number) r.get("cost")).doubleValue())
                .sum();

        model.addAttribute("cartItems", list);
        model.addAttribute("cartEmpty", list.isEmpty());
        model.addAttribute("totalPrice", total);
        return "cart";
    }

    // Отмена одной брони
    @PostMapping("/cart/cancel/{reservationId}")
    public String cancelReservation(
            @PathVariable("reservationId") Integer reservationId,
            HttpSession session
    ) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Integer.class,
                username
        );
        jdbc.update(
                "UPDATE reservations " +
                        "   SET status = 'CANCELLED' " +
                        " WHERE id = ? AND user_id = ?",
                reservationId, userId
        );
        return "redirect:/cart";
    }

    // Оплата всех PENDING-бронирований
    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        // Получаем id пользователя
        Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Integer.class,
                username
        );
        // 1) Сначала получаем все PENDING-бронирования и считаем стоимость
        List<Map<String,Object>> pending = jdbc.queryForList(
                "SELECT r.id AS reservationId,\n" +
                        "       COALESCE(c.price, co.price) AS resourcePrice,\n" +
                        "       r.start_time, r.end_time\n" +
                        "  FROM reservations r\n" +
                        "  LEFT JOIN computers c ON r.resource_type='COMPUTER' AND r.resource_id=c.id\n" +
                        "  LEFT JOIN consoles  co ON r.resource_type='CONSOLE'  AND r.resource_id=co.id\n" +
                        " WHERE r.user_id = ?\n" +
                        "   AND r.status  = 'PENDING'",
                userId
        );
        for (Map<String,Object> r : pending) {
            LocalDateTime st = (LocalDateTime) r.get("start_time");
            LocalDateTime en = (LocalDateTime) r.get("end_time");
            long hours = Duration.between(st, en).toHours();
            double price = ((Number) r.get("resourcePrice")).doubleValue();
            double cost = price * hours;
            int reservationId = ((Number) r.get("reservationId")).intValue();
            // 2) Вставляем запись об оплате
            jdbc.update(
                    "INSERT INTO payments (reservation_id, amount, paid_at, method)\n" +
                            "VALUES (?, ?, ?, ?)",
                    reservationId,
                    cost,
                    Timestamp.valueOf(LocalDateTime.now()),
                    "CARD"
            );
        }
        // 3) Обновляем статус бронирований на CONFIRMED
        jdbc.update(
                "UPDATE reservations\n" +
                        "   SET status = 'CONFIRMED'\n" +
                        " WHERE user_id = ? AND status = 'PENDING'",
                userId
        );
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout-balance")
    public String checkoutBalance(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        Integer userId = jdbc.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Integer.class,
                username
        );
        // Получаем все PENDING-бронирования и считаем стоимость
        List<Map<String,Object>> pending = jdbc.queryForList(
                "SELECT r.id AS reservationId,\n" +
                        "       COALESCE(c.price, co.price) AS resourcePrice,\n" +
                        "       r.start_time, r.end_time\n" +
                        "  FROM reservations r\n" +
                        "  LEFT JOIN computers c ON r.resource_type='COMPUTER' AND r.resource_id=c.id\n" +
                        "  LEFT JOIN consoles  co ON r.resource_type='CONSOLE'  AND r.resource_id=co.id\n" +
                        " WHERE r.user_id = ?\n" +
                        "   AND r.status  = 'PENDING'",
                userId
        );
        double total = 0.0;
        for (Map<String,Object> r : pending) {
            LocalDateTime st = (LocalDateTime) r.get("start_time");
            LocalDateTime en = (LocalDateTime) r.get("end_time");
            long hours = java.time.Duration.between(st, en).toHours();
            double price = ((Number) r.get("resourcePrice")).doubleValue();
            double cost = price * hours;
            r.put("cost", cost);
            total += cost;
        }
        // Проверяем баланс
        Double balance = jdbc.queryForObject("SELECT balance FROM users WHERE id = ?", Double.class, userId);
        if (balance == null || balance < total) {
            model.addAttribute("errorMsg", "Недостаточно средств на балансе для оплаты заказа.");
            return viewCart(session, model);
        }
        // Списываем средства
        jdbc.update("UPDATE users SET balance = balance - ? WHERE id = ?", total, userId);
        // Создаём платежи и подтверждаем бронирования
        for (Map<String,Object> r : pending) {
            double cost = ((Number) r.get("cost")).doubleValue();
            int reservationId = ((Number) r.get("reservationId")).intValue();
            jdbc.update(
                    "INSERT INTO payments (reservation_id, amount, paid_at, method)\n" +
                            "VALUES (?, ?, ?, ?)",
                    reservationId,
                    cost,
                    Timestamp.valueOf(LocalDateTime.now()),
                    "BALANCE"
            );
        }
        jdbc.update(
                "UPDATE reservations\n" +
                        "   SET status = 'CONFIRMED'\n" +
                        " WHERE user_id = ? AND status = 'PENDING'",
                userId
        );
        return "redirect:/cart";
    }
}
