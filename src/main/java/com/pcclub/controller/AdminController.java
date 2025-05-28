package com.pcclub.controller;

import com.pcclub.model.*;
import com.pcclub.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private ComputerRepository computerRepo;
    @Autowired private ConsoleRepository consoleRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private EventParticipantRepository partRepo;
    @Autowired private ReservationRepository    resRepo;
    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired private ReviewRepository reviewRepo;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("username", session.getAttribute("username"));

        // load all tables
        model.addAttribute("computers", computerRepo.findAll());
        model.addAttribute("consoles",  consoleRepo.findAll());
        model.addAttribute("users",     userRepo.findAll());
        model.addAttribute("events",    eventRepo.findAll());
        model.addAttribute("participants", partRepo.findAll());
        model.addAttribute("reservations", resRepo.findAll());
        model.addAttribute("payments", paymentRepo.findAll());
        model.addAttribute("reviews", reviewRepo.findAll());

        // Статистика
        long userCount = userRepo.count();
        long reservationCount = resRepo.count();
        long paymentCount = paymentRepo.count();
        java.math.BigDecimal totalPayments = paymentRepo.findAll().stream()
            .map(p -> p.getAmount() == null ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(p.getAmount().toString()))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("userCount", userCount);
        model.addAttribute("reservationCount", reservationCount);
        model.addAttribute("paymentCount", paymentCount);
        model.addAttribute("totalPayments", totalPayments);

        return "admin/dashboard";
    }

    // --- Компьютеры ---
    @GetMapping("/admin/computers/edit/{id}")
    public String editComputerForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("computer", computerRepo.findById(id).orElseThrow());
        return "admin/computers/edit";
    }
    @PostMapping("/admin/computers/edit/{id}")
    public String updateComputer(@PathVariable Integer id, HttpSession session, @ModelAttribute Computer computer) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        computer.setId(id);
        computerRepo.save(computer);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/computers/create")
    public String createComputerForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("computer", new Computer());
        return "admin/computers/create";
    }
    @PostMapping("/admin/computers/create")
    public String createComputer(HttpSession session, @ModelAttribute Computer computer) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        computerRepo.save(computer);
        return "redirect:/admin/dashboard";
    }

    // --- Консоли ---
    @GetMapping("/admin/consoles/edit/{id}")
    public String editConsoleForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("console", consoleRepo.findById(id).orElseThrow());
        return "admin/consoles/edit";
    }
    @PostMapping("/admin/consoles/edit/{id}")
    public String updateConsole(@PathVariable Integer id, HttpSession session, @ModelAttribute Console console) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        console.setId(id);
        consoleRepo.save(console);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/consoles/create")
    public String createConsoleForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("console", new Console());
        return "admin/consoles/create";
    }
    @PostMapping("/admin/consoles/create")
    public String createConsole(HttpSession session, @ModelAttribute Console console) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        consoleRepo.save(console);
        return "redirect:/admin/dashboard";
    }

    // --- Пользователи ---
    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("user", userRepo.findById(id).orElseThrow());
        return "admin/users/edit";
    }
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable Integer id, HttpSession session, @ModelAttribute User user) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        user.setId(id);
        userRepo.save(user);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        userRepo.deleteById(id);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/users/create")
    public String createUserForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("user", new User());
        return "admin/users/create";
    }
    @PostMapping("/admin/users/create")
    public String createUser(HttpSession session, @ModelAttribute User user) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        userRepo.save(user);
        return "redirect:/admin/dashboard";
    }

    // --- События ---
    @GetMapping("/admin/events/edit/{id}")
    public String editEventForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("event", eventRepo.findById(id).orElseThrow());
        return "admin/events/edit";
    }
    @PostMapping("/admin/events/edit/{id}")
    public String updateEvent(@PathVariable Integer id, HttpSession session, @ModelAttribute Event event) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        event.setId(id);
        eventRepo.save(event);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/events/create")
    public String createEventForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("event", new Event());
        return "admin/events/create";
    }
    @PostMapping("/admin/events/create")
    public String createEvent(HttpSession session, @ModelAttribute Event event) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        eventRepo.save(event);
        return "redirect:/admin/dashboard";
    }

    // --- Участники событий ---
    @GetMapping("/admin/event-participants/edit/{eventId}/{userId}")
    public String editParticipantForm(@PathVariable Integer eventId, @PathVariable Integer userId, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        EventParticipantId pid = new EventParticipantId(eventId, userId);
        model.addAttribute("participant", partRepo.findById(pid).orElseThrow());
        return "admin/event-participants/edit";
    }
    @PostMapping("/admin/event-participants/edit/{eventId}/{userId}")
    public String updateParticipant(@PathVariable Integer eventId, @PathVariable Integer userId, HttpSession session, @ModelAttribute EventParticipant participant) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        EventParticipantId pid = new EventParticipantId(eventId, userId);
        participant.setId(pid);
        partRepo.save(participant);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/participants/create")
    public String createParticipantForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("participant", new EventParticipant());
        model.addAttribute("events", eventRepo.findAll());
        model.addAttribute("users", userRepo.findAll());
        return "admin/event-participants/create";
    }
    @PostMapping("/admin/participants/create")
    public String createParticipant(HttpSession session, @ModelAttribute EventParticipant participant) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        partRepo.save(participant);
        return "redirect:/admin/dashboard";
    }

    // --- Бронирования ---
    @GetMapping("/admin/reservations/edit/{id}")
    public String editReservationForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("reservation", resRepo.findById(id).orElseThrow());
        return "admin/reservations/edit";
    }
    @PostMapping("/admin/reservations/edit/{id}")
    public String updateReservation(@PathVariable Integer id, HttpSession session, @ModelAttribute Reservation reservation) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        reservation.setId(id);
        resRepo.save(reservation);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/reservations/create")
    public String createReservationForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("computers", computerRepo.findAll());
        model.addAttribute("consoles", consoleRepo.findAll());
        return "admin/reservations/create";
    }
    @PostMapping("/admin/reservations/create")
    public String createReservation(HttpSession session, @ModelAttribute Reservation reservation, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        resRepo.save(reservation);
        return "redirect:/admin/dashboard";
    }

    // --- Платежи ---
    @GetMapping("/admin/payments/edit/{id}")
    public String editPaymentForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("payment", paymentRepo.findById(id).orElseThrow());
        return "admin/payments/edit";
    }
    @PostMapping("/admin/payments/edit/{id}")
    public String updatePayment(@PathVariable Integer id, HttpSession session, @ModelAttribute Payment payment) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        payment.setId(id);
        paymentRepo.save(payment);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/payments/create")
    public String createPaymentForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("payment", new Payment());
        model.addAttribute("reservations", resRepo.findAll());
        return "admin/payments/create";
    }
    @PostMapping("/admin/payments/create")
    public String createPayment(HttpSession session, @ModelAttribute Payment payment) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        paymentRepo.save(payment);
        return "redirect:/admin/dashboard";
    }

    // --- Экспорт ---
    @GetMapping("/admin/export/users")
    @ResponseBody
    public ResponseEntity<byte[]> exportUsersCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Username,Full Name,Email,Role\n");
        for (User u : userRepo.findAll()) {
            sb.append(u.getId()).append(",")
              .append(u.getUsername()).append(",")
              .append(u.getFullName() != null ? u.getFullName() : "").append(",")
              .append(u.getEmail() != null ? u.getEmail() : "").append(",")
              .append(u.getRole()).append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/export/reservations")
    @ResponseBody
    public ResponseEntity<byte[]> exportReservationsCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,UserID,ResourceType,ResourceID,StartTime,EndTime,Status\n");
        for (Reservation r : resRepo.findAll()) {
            sb.append(r.getId()).append(",")
              .append(r.getUser() != null ? r.getUser().getId() : "").append(",")
              .append(r.getResourceType()).append(",")
              .append(r.getResourceId()).append(",")
              .append(r.getStartTime()).append(",")
              .append(r.getEndTime()).append(",")
              .append(r.getStatus()).append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/export/payments")
    @ResponseBody
    public ResponseEntity<byte[]> exportPaymentsCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,ReservationID,Amount,PaidAt,Method\n");
        for (Payment p : paymentRepo.findAll()) {
            sb.append(p.getId()).append(",")
              .append(p.getReservation() != null ? p.getReservation().getId() : "").append(",")
              .append(p.getAmount()).append(",")
              .append(p.getPaidAt()).append(",")
              .append(p.getMethod()).append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/export/participants")
    @ResponseBody
    public ResponseEntity<byte[]> exportParticipantsCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("EventID,UserID,RegisteredAt,Result\n");
        for (EventParticipant p : partRepo.findAll()) {
            sb.append(p.getId() != null ? p.getId().getEventId() : "").append(",")
              .append(p.getId() != null ? p.getId().getUserId() : "").append(",")
              .append(p.getRegisteredAt()).append(",")
              .append(p.getResult() != null ? p.getResult() : "").append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=participants.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/export/computers")
    @ResponseBody
    public ResponseEntity<byte[]> exportComputersCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,CPU,GPU,RAM,Price,Available\n");
        for (Computer c : computerRepo.findAll()) {
            sb.append(c.getId()).append(",")
              .append(c.getName()).append(",")
              .append(c.getCpuModel() != null ? c.getCpuModel() : "").append(",")
              .append(c.getGpuModel() != null ? c.getGpuModel() : "").append(",")
              .append(c.getRamGb() != null ? c.getRamGb() : "").append(",")
              .append(c.getPrice() != null ? c.getPrice() : "").append(",")
              .append(c.getIsAvailable() != null ? c.getIsAvailable() : "").append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=computers.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/export/consoles")
    @ResponseBody
    public ResponseEntity<byte[]> exportConsolesCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Type,Manufacturer,Price,Available\n");
        for (Console co : consoleRepo.findAll()) {
            sb.append(co.getId()).append(",")
              .append(co.getName() != null ? co.getName() : "").append(",")
              .append(co.getType() != null ? co.getType() : "").append(",")
              .append(co.getManufacturer() != null ? co.getManufacturer() : "").append(",")
              .append(co.getPrice() != null ? co.getPrice() : "").append(",")
              .append(co.getIsAvailable() != null ? co.getIsAvailable() : "").append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=consoles.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Integer id) {
        reviewRepo.deleteById(id);
        return "redirect:/admin/dashboard#reviews";
    }

    @GetMapping("/admin/reviews/create")
    public String createReviewForm(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("review", new Review());
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("computers", computerRepo.findAll());
        model.addAttribute("consoles", consoleRepo.findAll());
        return "admin/reviews/create";
    }
    @PostMapping("/admin/reviews/create")
    public String createReview(HttpSession session, @ModelAttribute Review review) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        reviewRepo.save(review);
        return "redirect:/admin/dashboard#reviews";
    }
    @GetMapping("/admin/reviews/edit/{id}")
    public String editReviewForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        model.addAttribute("review", reviewRepo.findById(id).orElseThrow());
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("computers", computerRepo.findAll());
        model.addAttribute("consoles", consoleRepo.findAll());
        return "admin/reviews/edit";
    }
    @PostMapping("/admin/reviews/edit/{id}")
    public String updateReview(@PathVariable Integer id, HttpSession session, @ModelAttribute Review review) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        review.setId(id);
        reviewRepo.save(review);
        return "redirect:/admin/dashboard#reviews";
    }
    @GetMapping("/admin/export/reviews")
    @ResponseBody
    public ResponseEntity<byte[]> exportReviewsCsv(HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(403).build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID,User,ResourceType,ResourceId,Rating,Text,CreatedAt\n");
        for (Review r : reviewRepo.findAll()) {
            sb.append(r.getId()).append(",")
              .append(r.getUser() != null ? r.getUser().getUsername() : "").append(",")
              .append(r.getResourceType()).append(",")
              .append(r.getResourceId()).append(",")
              .append(r.getRating()).append(",")
              .append(r.getText() != null ? r.getText().replaceAll(",", " ") : "").append(",")
              .append(r.getCreatedAt()).append("\n");
        }
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reviews.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/admin/reviews/search")
    public String searchReviews(@RequestParam String query, HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login?error";
        }
        List<Review> reviews = reviewRepo.findAll().stream().filter(r ->
            (r.getText() != null && r.getText().toLowerCase().contains(query.toLowerCase())) ||
            (r.getUser() != null && r.getUser().getUsername() != null && r.getUser().getUsername().toLowerCase().contains(query.toLowerCase())) ||
            (r.getResourceType() != null && r.getResourceType().name().toLowerCase().contains(query.toLowerCase())) ||
            (r.getResourceId() != null && r.getResourceId().toString().contains(query)) ||
            (Integer.toString(r.getRating()).contains(query)) ||
            (r.getCreatedAt() != null && r.getCreatedAt().toString().contains(query))
        ).toList();
        model.addAttribute("reviews", reviews);
        model.addAttribute("searchQuery", query);
        return "admin/dashboard";
    }
}

