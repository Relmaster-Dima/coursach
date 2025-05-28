package com.pcclub.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Map<String, Object> getResources() {
        List<Map<String, Object>> computers = jdbcTemplate.queryForList(
                "SELECT * FROM computers WHERE is_available = 1"
        );

        List<Map<String, Object>> consoles = jdbcTemplate.queryForList(
                "SELECT * FROM consoles WHERE is_available = 1"
        );

        Map<String, Object> result = new HashMap<>();
        result.put("computers", computers);
        result.put("consoles", consoles);

        return result;
    }
}
