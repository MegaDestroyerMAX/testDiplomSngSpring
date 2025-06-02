package com.example.testdiplomsngspring.controller;

import com.example.testdiplomsngspring.service.ConnectionManager;
import com.example.testdiplomsngspring.service.ConnectionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/db")
public class ConnectionController {
    private final ConnectionManager connectionManager;

    @Autowired
    public ConnectionController(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connectToDatabase(@RequestParam String ip) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Проверяем валидность IP (базовая проверка)
            if (ip == null || ip.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "IP-адрес не может быть пустым");
                return ResponseEntity.badRequest().body(response);
            }

            //Автоматическая проверка(Проверяем существующее подклбчение перед созданием нового)
            boolean hadActiveConnection = connectionManager.hasActiveConnection(ip);

            // Создаем новое подключение (перезаписывает существующее)
            ConnectionWrapper wrapper = connectionManager.setConnection(ip);

            response.put("success", true);
            response.put("message", hadActiveConnection ? "Подключение обновлено для IP: " + ip : "Новое подключение создано для IP: " + ip);
            response.put("ip", ip);
            response.put("wasActive", hadActiveConnection);
            response.put("activeConnections", connectionManager.getActiveConnectionsCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка подключения: " + e.getMessage());
            response.put("ip", ip);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/connection/{ip}")
    public ResponseEntity<Map<String, Object>> getConnection(@PathVariable String ip) {
        Map<String, Object> response = new HashMap<>();

        try {
            // АВТОМАТИЧЕСКАЯ ПРОВЕРКА: getConnection автоматически проверит истекшие соединения
            boolean wasActive = connectionManager.hasActiveConnection(ip);
            // Получаем подключение (создаст новое, если нужно)
            ConnectionWrapper wrapper = connectionManager.getConnection(ip);
            boolean isNewConnection = !wasActive;

            response.put("success", true);
            response.put("message", isNewConnection ?
                    "Создано новое подключение для IP: " + ip :
                    "Подключение получено успешно для IP: " + ip);
            response.put("ip", ip);
            response.put("hasConnection", true);
            response.put("isNewConnection", isNewConnection);
            response.put("wasExpired", wasActive && isNewConnection); // было активно, но истекло
            response.put("activeConnections", connectionManager.getActiveConnectionsCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка получения подключения: " + e.getMessage());
            response.put("ip", ip);
            response.put("hasConnection", false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/status/{ip}")
    public ResponseEntity<Map<String, Object>> getConnectionStatus(@PathVariable String ip) {
        Map<String, Object> response = new HashMap<>();


        // АВТОМАТИЧЕСКАЯ ПРОВЕРКА: hasActiveConnection проверяет не только наличие, но и срок действия
        boolean hasActiveConnection = connectionManager.hasActiveConnection(ip);

        response.put("ip", ip);
        response.put("hasActiveConnection", hasActiveConnection);
        response.put("status", hasActiveConnection ? "ACTIVE" : "INACTIVE");
        response.put("checkTime", java.time.LocalDateTime.now().toString());
        response.put("totalActiveConnections", connectionManager.getActiveConnectionsCount());

        if (hasActiveConnection) {
            response.put("message", "Подключение активно и не истекло");
        } else {
            response.put("message", "Подключение отсутствует или истекло (10+ минут без активности)");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupConnections() {
        Map<String, Object> response = new HashMap<>();

        try {

            // Запускаем принудительную очистку
            int beforeCount = connectionManager.getActiveConnectionsCount();
            connectionManager.cleanupExpiredConnections();
            int afterCount = connectionManager.getActiveConnectionsCount();

            int removedCount = beforeCount - afterCount;

            response.put("success", true);
            response.put("message", removedCount > 0 ?
                    "Очистка завершена. Удалено истекших подключений: " + removedCount :
                    "Очистка завершена. Истекших подключений не найдено");
            response.put("removedConnections", removedCount);
            response.put("remainingConnections", afterCount);
            response.put("cleanupTime", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при очистке: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
