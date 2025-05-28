package com.pcclub.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.pcclub.model.Reservation;
import com.pcclub.model.ReservationStatus;
import com.pcclub.model.Payment;
import com.pcclub.model.PaymentMethod;
import com.pcclub.repository.ReservationRepository;
import com.pcclub.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import com.pcclub.repository.ComputerRepository;
import com.pcclub.repository.ConsoleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import com.pcclub.model.User;
import com.pcclub.model.Computer;
import com.pcclub.model.Console;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Controller
public class ManagerController {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private ConsoleRepository consoleRepository;

    @GetMapping("/manager/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        if (!"MANAGER".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("username", session.getAttribute("username"));
        // Получаем все PENDING-брони
        List<Reservation> pendingReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .toList();
        model.addAttribute("pendingReservations", pendingReservations);
        return "manager/dashboard";
    }

    @PostMapping("/manager/confirm-payment/{reservationId}")
    @Transactional
    public String confirmPayment(@PathVariable Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null || reservation.getStatus() != ReservationStatus.PENDING) {
            return "redirect:/manager/dashboard?error";
        }
        // Считаем стоимость
        long hours = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours();
        BigDecimal pricePerHour = BigDecimal.ZERO;
        if (reservation.getResourceType().name().equals("COMPUTER")) {
            var computer = computerRepository.findById(reservation.getResourceId()).orElse(null);
            if (computer != null) pricePerHour = BigDecimal.valueOf(computer.getPrice());
        } else if (reservation.getResourceType().name().equals("CONSOLE")) {
            var console = consoleRepository.findById(reservation.getResourceId()).orElse(null);
            if (console != null && console.getPrice() != null) pricePerHour = new BigDecimal(console.getPrice());
        }
        BigDecimal total = pricePerHour.multiply(BigDecimal.valueOf(hours));
        // Обновляем статус
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        // Создаём платёж
        Payment payment = new Payment(reservation, total, LocalDateTime.now(), PaymentMethod.CASH);
        paymentRepository.save(payment);
        return "redirect:/manager/dashboard";
    }

    // (A) Все бронирования
    @GetMapping("/manager/reservations")
    public String allReservations(HttpSession session, Model model) {
        if (!"MANAGER".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        List<Reservation> allReservations = reservationRepository.findAll();
        model.addAttribute("allReservations", allReservations);
        return "manager/reservations";
    }

    @PostMapping("/manager/cancel-reservation/{reservationId}")
    @Transactional
    public String cancelReservation(@PathVariable Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return "redirect:/manager/reservations?error";
        if (reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.CONFIRMED) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
        return "redirect:/manager/reservations";
    }

    // (B) История оплат с фильтрацией
    @GetMapping("/manager/payments")
    public String paymentsHistory(HttpSession session, Model model,
                                 @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                 @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                 @RequestParam(value = "method", required = false) String method) {
        if (!"MANAGER".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        List<Payment> payments = paymentRepository.findAll();
        if (from != null) {
            payments = payments.stream().filter(p -> !p.getPaidAt().toLocalDate().isBefore(from)).collect(Collectors.toList());
        }
        if (to != null) {
            payments = payments.stream().filter(p -> !p.getPaidAt().toLocalDate().isAfter(to)).collect(Collectors.toList());
        }
        if (method != null && !method.isEmpty()) {
            payments = payments.stream().filter(p -> p.getMethod().name().equalsIgnoreCase(method)).collect(Collectors.toList());
        }
        model.addAttribute("payments", payments);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("method", method);
        return "manager/payments";
    }

    // (C) Свободные ресурсы
    @GetMapping("/manager/available-resources")
    public String availableResources(HttpSession session, Model model) {
        if (!"MANAGER".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        List<Computer> computers = computerRepository.findByIsAvailableTrue();
        List<Console> consoles = consoleRepository.findByIsAvailableTrue();
        model.addAttribute("computers", computers);
        model.addAttribute("consoles", consoles);
        return "manager/available-resources";
    }
}
