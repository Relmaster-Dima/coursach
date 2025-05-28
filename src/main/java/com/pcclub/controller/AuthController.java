package com.pcclub.controller;

import com.pcclub.model.User;
import com.pcclub.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Форма входа
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // Обработка входа
    @PostMapping("/login")
    public String loginSubmit(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        User user = userService.authenticate(username, password);
        if (user == null) {
            model.addAttribute("errorMsg", "Неверное имя пользователя или пароль");
            return "login";
        }
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole().toString());
        return "redirect:/";
    }

    // Форма регистрации
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Обработка регистрации
    @PostMapping("/register")
    public String registerSubmit(
            @ModelAttribute User user,
            Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMsg", "Имя пользователя уже занято");
            return "register";
        }
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMsg", "Email уже зарегистрирован");
            return "register";
        }
        userService.register(user);
        model.addAttribute("msg", "Регистрация успешна. Пожалуйста, войдите.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "Пользователь с таким email не найден");
            return "forgot-password";
        }
        // Генерируем новый пароль
        String newPassword = generatePassword(8);
        user.setPassword(newPassword);
        userService.update(user);
        model.addAttribute("message", "Ваш новый пароль: " + newPassword);
        return "forgot-password";
    }

    private String generatePassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
