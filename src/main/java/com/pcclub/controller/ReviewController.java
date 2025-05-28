package com.pcclub.controller;

import com.pcclub.model.Review;
import com.pcclub.model.ResourceType;
import com.pcclub.model.User;
import com.pcclub.service.ReviewService;
import com.pcclub.service.UserService;
import com.pcclub.service.ComputerService;
import com.pcclub.service.ConsoleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private ComputerService computerService;
    @Autowired
    private ConsoleService consoleService;

    @GetMapping("/reviews/{type}/{id}")
    public String reviewsForResource(@PathVariable("type") ResourceType type,
                                     @PathVariable("id") Integer resourceId,
                                     Model model) {
        List<Review> reviews = reviewService.getReviewsForResource(type, resourceId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        List<String> reviewNames = reviews == null ? java.util.Collections.emptyList() : reviews.stream().map(r -> {
            if (r.getResourceType() == com.pcclub.model.ResourceType.COMPUTER) {
                var c = computerService.getComputerById(r.getResourceId());
                return c != null ? c.getName() : "ПК #" + r.getResourceId();
            } else if (r.getResourceType() == com.pcclub.model.ResourceType.CONSOLE) {
                var co = consoleService.getConsoleById(r.getResourceId());
                return co != null ? co.getName() : "Консоль #" + r.getResourceId();
            } else {
                return "#" + r.getResourceId();
            }
        }).collect(java.util.stream.Collectors.toList());
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avg);
        model.addAttribute("resourceType", type);
        model.addAttribute("resourceId", resourceId);
        model.addAttribute("reviewNames", reviewNames);
        return "reviews";
    }

    @PostMapping("/reviews/add")
    public String addReview(@ModelAttribute Review review, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userService.findById(userId);
        review.setUser(user);
        reviewService.save(review);
        return "redirect:/reviews/" + review.getResourceType() + "/" + review.getResourceId();
    }

    @GetMapping("/reviews")
    public String allReviews(Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        List<String> reviewNames = reviews == null ? java.util.Collections.emptyList() : reviews.stream().map(r -> {
            if (r.getResourceType() == com.pcclub.model.ResourceType.COMPUTER) {
                var c = computerService.getComputerById(r.getResourceId());
                return c != null ? c.getName() : "ПК #" + r.getResourceId();
            } else if (r.getResourceType() == com.pcclub.model.ResourceType.CONSOLE) {
                var co = consoleService.getConsoleById(r.getResourceId());
                return co != null ? co.getName() : "Консоль #" + r.getResourceId();
            } else {
                return "#" + r.getResourceId();
            }
        }).collect(java.util.stream.Collectors.toList());
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avg);
        model.addAttribute("resourceType", null);
        model.addAttribute("resourceId", null);
        model.addAttribute("reviewNames", reviewNames);
        return "reviews";
    }
} 