//package com.example.testdiplomsngspring.controller;
//
//import com.example.testdiplomsngspring.service.ConnectionManager;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/database")
//public class DatabaseController {
//
//    private final ConnectionManager connectionManager;
//    private final JdbcTemplate jdbcTemplate;
//
//    public DatabaseController(ConnectionManager connectionManager, JdbcTemplate jdbcTemplate) {
//        this.connectionManager = connectionManager;
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    //@PostMapping("/connect")
////    public ResponseEntity<String> connect(@RequestParam String ip) {
//////        JdbcTemplate jdbcTemplate = createTemplateForIp(ip);
//////        connectionManager.setConnection(ip, jdbcTemplate);
////        System.out.println("Connecting to " + ip);
////        connectionManager.setConnection(ip, jdbcTemplate);
////        return ResponseEntity.ok("Подключение установлено для IP: " + ip);
////    }
//}
