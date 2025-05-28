// src/main/java/com/pcclub/controller/EventController.java
package com.pcclub.controller;

import com.pcclub.model.Event;
import com.pcclub.model.User;
import com.pcclub.service.UserService;
import com.pcclub.service.EventService;
import com.pcclub.service.EventParticipantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class EventController {
    @Autowired
    private UserService userService;
    @Autowired private EventService eventService;
    @Autowired private EventParticipantService partService;
    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable int id, Model model, Principal principal) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);

        // Защита от NullPointerException, если пользователь не авторизован
        if (principal != null) {
            model.addAttribute("user", userService.findByUsername(principal.getName()));
        }
        return "event-details"; // <-- имя thymeleaf-шаблона в src/main/resources/templates/
    }

    // этот метод не обязателен, но можно сделать отдельную страницу /events
    @GetMapping("/events")
    public String eventsPage(Model model) {
        model.addAttribute("events", eventService.getUpcomingEvents());
        return "events";
    }

    @PostMapping("/events/register/{id}")
    public String registerEvent(@PathVariable("id") Integer id, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        boolean alreadyRegistered = partService.isRegistered(user.getId(), id);
        if (alreadyRegistered) {
            redirectAttributes.addFlashAttribute("message", "Вы уже записаны на это событие!");
            return "redirect:/";
        } else {
            partService.register(user.getId(), id);
            redirectAttributes.addFlashAttribute("message", "Вы успешно записались на событие!");
            return "redirect:/";
        }
    }
}
