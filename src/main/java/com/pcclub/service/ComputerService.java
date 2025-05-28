package com.pcclub.service;

import com.pcclub.model.Computer;
import com.pcclub.repository.ComputerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComputerService {
    private final ComputerRepository computerRepository;

    public ComputerService(ComputerRepository computerRepository) {
        this.computerRepository = computerRepository;
    }

    public List<Computer> getAvailableComputers() {
        return computerRepository.findByIsAvailableTrue();
    }

    public List<Computer> getAvailableBetween(LocalDateTime start, LocalDateTime end) {
        return computerRepository.findAvailableBetween(start, end);
    }

    public Computer getComputerById(Integer id) {
        return computerRepository.findById(id).orElse(null);
    }

    public List<Computer> getComputersByIds(List<Integer> ids) {
        return computerRepository.findAllById(ids);
    }
}
