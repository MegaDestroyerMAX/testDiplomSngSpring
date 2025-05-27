package com.example.testdiplomsngspring.controller;

import com.example.testdiplomsngspring.service.ConnectionManager;
import com.example.testdiplomsngspring.service.ConnectionRequest;
//import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import javax.sql.DataSource;

//@RestController
//@RequestMapping("/api")

public class ExcelController {
//    private final DataSource dataSource;

    // Spring автоматически внедрит зависимость через конструктор
//    public ExcelController(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

//    @Value("${db.default.username}")
//    private String defaultUsername;
//
//    @Value("${db.default.password}")
//    private String defaultPassword;

    //private final ConnectionManager connectionManager;

//    public ExcelController(ConnectionManager connectionManager) {
//        this.connectionManager = connectionManager;
//    }

//    @PostMapping("/connect")
//    public ResponseEntity<String> connectToDatabase(@RequestBody ConnectionRequest request, HttpSession session) {
//        String ip = request.getIp();
//        String code = request.getCode();
//
//        if (ip == null || ip.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("IP адрес не должен быть пустым");
//        }
//
//        String url = "jdbc:postgresql://" + ip + ":5432/my_database";
//
//        try {
//            Connection conn = DriverManager.getConnection(url, defaultUsername, defaultPassword);
//            String sessionId = session.getId();
//            connectionManager.setConnection(sessionId, conn);
//            return ResponseEntity.ok("Успешное подключение для сессии: " + sessionId);
//            //            if (conn != null && !conn.isClosed()) {
//            //                return ResponseEntity.ok("Успешное подключение к " + ip);
//            //            } else {
//            //                return ResponseEntity.status(500).body("Не удалось подключиться к " + ip);
//            //            }
//        }catch (SQLException e) {
//            return ResponseEntity.status(500).body("Ошибка подключения: " + e.getMessage());
//        }
//    }

//    @PostMapping("/disconnect")
//    public ResponseEntity<String> disconnect(@RequestParam String userId) {
//        connectionManager.disconnect(userId);
//        return ResponseEntity.ok("Пользователь " + userId + " отключён от базы данных");
//    }
}
