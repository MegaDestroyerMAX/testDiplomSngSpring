package com.example.testdiplomsngspring.controller;

import com.example.testdiplomsngspring.service.ConnectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

@RestController
@RequestMapping("/api")

public class ExcelController {
    private final DataSource dataSource;

    // Spring автоматически внедрит зависимость через конструктор
    public ExcelController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connectToDatabase(@RequestBody ConnectionRequest request) {
        String ip = request.getIp();
        String code = request.getCode();


        String url = "jdbc:postgresql://" + ip + ":5432/my_database";
//        System.out.println("Connecting to " + url);
//        String username = "postgres";
//        String password = "111";

        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return ResponseEntity.ok("Успешное подключение к " + ip);
            } else {
                return ResponseEntity.status(500).body("Не удалось подключиться к " + ip);
            }
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Ошибка подключения: " + e.getMessage());
        }

//        try (Connection conn = DriverManager.getConnection(url, username, password)) {
//            if (conn != null && !conn.isClosed()) {
//                return ResponseEntity.ok("Успешное подключение к " + ip);
//            } else {
//                return ResponseEntity.status(500).body("Не удалось подключиться к " + ip);
//            }
//        } catch (SQLException e) {
//            return ResponseEntity.status(500).body("Ошибка подключения: " + e.getMessage());
//        }
    }
}
