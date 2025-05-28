package com.pcclub.service;

import com.pcclub.model.Reservation;
import com.pcclub.model.Role;
import com.pcclub.model.User;
import com.pcclub.model.ResourceType;
import com.pcclub.repository.ReservationRepository;
import com.pcclub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User register(User user) {
        user.setRole(Role.CLIENT);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден: " + username));
    }
    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден: " + id));
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public List<Reservation> getUserReservations(int userId) {
        // получим сущность User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден: " + userId));
        // а теперь JPA найдёт все резервации
        return reservationRepository.findByUser(user);
        // или, если вы выбрали findByUser_Id:
        // return reservationRepository.findByUser_Id(userId);
    }

    public Reservation updateReservation(Reservation r) {
        return reservationRepository.save(r);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<Reservation> getReservationsForResource(ResourceType type, Integer resourceId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getResourceType() == type && r.getResourceId().equals(resourceId))
                .toList();
    }
}
