package com.pcclub.controller;

import com.pcclub.dto.ReservationView;
import com.pcclub.model.User;
import com.pcclub.model.Reservation;
import com.pcclub.model.Event;
import com.pcclub.service.ComputerService;
import com.pcclub.service.ConsoleService;
import com.pcclub.service.UserService;
import com.pcclub.service.EventParticipantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Controller
public class ProfileController {
    @Autowired
    private ComputerService computerService;
    @Autowired
    private ConsoleService consoleService;


    @Autowired
    private UserService userService;
    @Autowired
    private EventParticipantService epService;

    // Показ профиля
    @GetMapping("/profile")
    public String getProfile(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        User user = userService.findById(userId);
        List<Reservation> reservations = userService.getUserReservations(userId);
        LocalDateTime now = LocalDateTime.now();
        List<ReservationView> rvList = reservations.stream().map(r -> {
            String name = switch (r.getResourceType()) {
                case COMPUTER -> computerService.getComputerById(r.getResourceId()).getName();
                case CONSOLE  -> consoleService.getConsoleById(r.getResourceId()).getName();
            };
            return new ReservationView(
                    r.getId(),
                    r.getResourceType(),
                    r.getResourceId(),
                    name,
                    r.getStartTime(),
                    r.getEndTime(),
                    r.getStatus()
            );
        }).toList();
        List<ReservationView> futureReservations = rvList.stream()
                .filter(r -> r.getStatus() == com.pcclub.model.ReservationStatus.CONFIRMED &&
                        (r.getStartTime().isAfter(now) ||
                         (r.getStartTime().isBefore(now) || r.getStartTime().isEqual(now)) && r.getEndTime().isAfter(now)))
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();
        List<ReservationView> pastReservations = rvList.stream()
                .filter(r -> !(r.getStatus() == com.pcclub.model.ReservationStatus.CONFIRMED &&
                        (r.getStartTime().isAfter(now) ||
                         (r.getStartTime().isBefore(now) || r.getStartTime().isEqual(now)) && r.getEndTime().isAfter(now))))
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .toList();
        List<Event> events = epService.getEventsForUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("futureReservations", futureReservations);
        model.addAttribute("pastReservations", pastReservations);
        model.addAttribute("now", now);
        model.addAttribute("events", events);
        return "profile";
    }

    // Сохранение изменений
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User form, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 1) подгружаем «живую» сущность из БД
        User existing = userService.findById(userId);

        // 2) в неё копируем только те поля, что редактирует пользователь
        existing.setFullName(form.getFullName());
        existing.setEmail(form.getEmail());
        // (если у вас есть смена пароля, обработайте его отдельно)

        // 3) сохраняем
        userService.update(existing);

        return "redirect:/profile";
    }

    @PostMapping("/profile/cancel/{id}")
    public String cancelReservation(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Reservation r = userService.getUserReservations(userId).stream()
                .filter(res -> res.getId().equals(id))
                .findFirst().orElse(null);
        if (r == null) return "redirect:/profile?error";
        LocalDateTime now = LocalDateTime.now();
        if (r.getStatus() == com.pcclub.model.ReservationStatus.CONFIRMED &&
            r.getStartTime().isAfter(now) &&
            java.time.Duration.between(now, r.getStartTime()).toHours() > 1) {
            r.setStatus(com.pcclub.model.ReservationStatus.CANCELLED);
            userService.updateReservation(r);
            // Возврат денег на баланс
            User user = userService.findById(userId);
            BigDecimal pricePerHour = BigDecimal.ZERO;
            if (r.getResourceType() == com.pcclub.model.ResourceType.COMPUTER) {
                pricePerHour = BigDecimal.valueOf(computerService.getComputerById(r.getResourceId()).getPrice());
            } else if (r.getResourceType() == com.pcclub.model.ResourceType.CONSOLE) {
                pricePerHour = BigDecimal.valueOf(consoleService.getConsoleById(r.getResourceId()).getPrice());
            }
            long hours = java.time.Duration.between(r.getStartTime(), r.getEndTime()).toHours();
            BigDecimal total = pricePerHour.multiply(BigDecimal.valueOf(hours));
            user.setBalance(user.getBalance().add(total));
            userService.update(user);
        }
        return "redirect:/profile";
    }
}
