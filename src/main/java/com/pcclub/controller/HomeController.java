package com.pcclub.controller;

import com.pcclub.model.Computer;
import com.pcclub.model.Console;
import com.pcclub.model.Event;
import com.pcclub.service.ComputerService;
import com.pcclub.service.ConsoleService;
import com.pcclub.service.EventService;
import com.pcclub.repository.ReviewRepository;
import com.pcclub.model.Review;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ComputerService computerService;
    private final ConsoleService consoleService;
    private final EventService eventService;
    private final ReviewRepository reviewRepository;

    public HomeController(
            ComputerService computerService,
            ConsoleService consoleService,
            EventService eventService,
            ReviewRepository reviewRepository
    ) {
        this.computerService = computerService;
        this.consoleService = consoleService;
        this.eventService = eventService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/")
    public String home(
            Model model,
            HttpSession session,
            @RequestParam(name = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam(name = "endTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {
        List<Computer> computers = (startTime != null && endTime != null)
                ? computerService.getAvailableBetween(startTime, endTime)
                : computerService.getAvailableComputers();

        List<Console> consoles = (startTime != null && endTime != null)
                ? consoleService.getAvailableBetween(startTime, endTime)
                : consoleService.getAvailableConsoles();

        // Загружаем все события
        List<Event> events = eventService.getAllEvents();

        List<Review> latestReviews = reviewRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        List<String> latestReviewNames = latestReviews.stream().map(r -> {
            if (r.getResourceType() == com.pcclub.model.ResourceType.COMPUTER) {
                Computer c = computerService.getComputerById(r.getResourceId());
                return c != null ? c.getName() : "ПК #" + r.getResourceId();
            } else if (r.getResourceType() == com.pcclub.model.ResourceType.CONSOLE) {
                Console co = consoleService.getConsoleById(r.getResourceId());
                return co != null ? co.getName() : "Консоль #" + r.getResourceId();
            } else {
                return "#" + r.getResourceId();
            }
        }).collect(Collectors.toList());

        model.addAttribute("computers", computers);
        model.addAttribute("consoles", consoles);
        model.addAttribute("events", events);
        model.addAttribute("session", session);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("latestReviews", latestReviews);
        model.addAttribute("latestReviewNames", latestReviewNames);

        // Сохраняем интервалы бронирования в сессии (если нужны)
        session.setAttribute("bookingStart", startTime);
        session.setAttribute("bookingEnd",   endTime);

        return "home";
    }
}
