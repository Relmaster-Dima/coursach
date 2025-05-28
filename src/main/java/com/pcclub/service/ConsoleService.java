package com.pcclub.service;

import com.pcclub.model.Console;
import com.pcclub.repository.ConsoleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsoleService {
    private final ConsoleRepository consoleRepository;

    public ConsoleService(ConsoleRepository consoleRepository) {
        this.consoleRepository = consoleRepository;
    }

    public List<Console> getAvailableConsoles() {
        return consoleRepository.findByIsAvailableTrue();
    }

    public List<Console> getAvailableBetween(LocalDateTime start, LocalDateTime end) {
        return consoleRepository.findAvailableBetween(start, end);
    }

    public Console getConsoleById(Integer id) {
        return consoleRepository.findById(id).orElse(null);
    }

    public List<Console> getConsolesByIds(List<Integer> ids) {
        return consoleRepository.findAllById(ids);
    }
}
