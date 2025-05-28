package com.pcclub.controller;

import com.pcclub.model.Reservation;
import com.pcclub.model.ResourceType;
import com.pcclub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class ScheduleController {
    @Autowired
    private UserService userService;

    @GetMapping("/schedule/{type}/{id}")
    public String resourceSchedule(@PathVariable("type") ResourceType type,
                                   @PathVariable("id") Integer resourceId,
                                   Model model) {
        List<Reservation> reservations = userService.getReservationsForResource(type, resourceId);
        model.addAttribute("reservations", reservations);
        model.addAttribute("type", type);
        model.addAttribute("resourceId", resourceId);
        return "schedule";
    }
} 