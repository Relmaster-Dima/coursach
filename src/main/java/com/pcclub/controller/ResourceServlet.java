package com.pcclub.controller;

import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/api/resources")
public class ResourceServlet extends HttpServlet {

    // Подключение к БД — укажи свои параметры!
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/computer_club";
        String user = "root";
        String pass = "root";
        return DriverManager.getConnection(url, user, pass);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        List<Map<String, Object>> computers = new ArrayList<>();
        List<Map<String, Object>> consoles = new ArrayList<>();

        try (Connection conn = getConnection()) {
            // ПК
            Statement stmt = conn.createStatement();
            ResultSet rsPC = stmt.executeQuery("SELECT * FROM computers WHERE is_available = 1");
            while (rsPC.next()) {
                Map<String, Object> pc = new HashMap<>();
                pc.put("name", rsPC.getString("name"));
                pc.put("cpu", rsPC.getString("cpu_model"));
                pc.put("gpu", rsPC.getString("gpu_model"));
                pc.put("ram", rsPC.getInt("ram_gb"));
                pc.put("price", rsPC.getDouble("price"));
                computers.add(pc);
            }
            rsPC.close();

            // Консоли
            ResultSet rsConsole = stmt.executeQuery("SELECT * FROM consoles WHERE is_available = 1");
            while (rsConsole.next()) {
                Map<String, Object> console = new HashMap<>();
                console.put("name", rsConsole.getString("name"));
                console.put("type", rsConsole.getString("type"));
                consoles.add(console);
            }
            rsConsole.close();
            stmt.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"DB error\"}");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("computers", computers);
        result.put("consoles", consoles);

        String json = new Gson().toJson(result);
        response.getWriter().write(json);
    }
}
