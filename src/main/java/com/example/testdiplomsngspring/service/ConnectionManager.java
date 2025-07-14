package com.example.testdiplomsngspring.service;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;

/**
 * Сервис управления подключениями пользователей через JdbcTemplate.
 * Хранит обёртки с jdbcTemplate и временем последнего обращения.
 * Удаляет неактивные соединения по таймауту.
 */
@Service
public class ConnectionManager {

    // Карта активных соединений: ключ — IP пользователя, значение — обёртка JdbcTemplate
    private final Map<String, ConnectionWrapper> connections = new ConcurrentHashMap<>();

    // Базовые параметры подключения из application.properties
    private final String baseUrl;
    private final String defaultUsername;
    private final String defaultPassword;

    public ConnectionManager(
            @Value("${spring.datasource.url}") String baseUrl,
            @Value("${db.default.username}") String username,
            @Value("${db.default.password}") String password) {
        this.baseUrl = baseUrl;
        this.defaultUsername = username;
        this.defaultPassword = password;
    }

    /*
     * Создает новое подключение для указанного IP
     * @param ip IP-адрес сервера БД
     * @return ConnectionWrapper для нового подключения
     */
    public ConnectionWrapper setConnection(String ip) {
        // Формируем URL для конкретного IP
        String dbUrl = baseUrl.replace("localhost", ip);
        System.out.println(dbUrl);

        // Создаем DataSource
        DataSource dataSource = DataSourceBuilder.create()
                .url(dbUrl)
                .username(defaultUsername)
                .password(defaultPassword)
                .build();

        // Создаем JdbcTemplate
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Создаем и сохраняем обертку
        ConnectionWrapper wrapper = new ConnectionWrapper(jdbcTemplate);
        connections.put(ip, wrapper);
        System.out.println(ip + " " + wrapper);

        return wrapper;
    }

    //Получает существующее подключение для IP или создает новое
    public ConnectionWrapper getConnection(String ip) {
        ConnectionWrapper wrapper = connections.get(ip);

        if (wrapper == null) {
            // Подключения нет - создаем новое
            System.out.println("AUTO-CHECK: No connection found for IP: " + ip + ", creating new one");
            return setConnection(ip);
        } else if (wrapper.isExpired()) {
            // Подключение истекло - удаляем и создаем новое
            connections.remove(ip);
            System.out.println("AUTO-CHECK: Connection expired for IP: " + ip + ", creating new one");
            return setConnection(ip);
        } else {
            // Подключение активно - продлеваем время жизни и возвращаем
            wrapper.extendLife();
            System.out.println("AUTO-CHECK: Connection active for IP: " + ip + ", extending life");
            return wrapper;
        }
    }

    //Получает информацию о состоянии всех подключений (для отладки)
    public String getConnectionsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Active connections: ").append(connections.size()).append("\n");

        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, ConnectionWrapper> entry : connections.entrySet()) {
            ConnectionWrapper wrapper = entry.getValue();
            info.append("IP: ").append(entry.getKey())
                    .append(", Expired: ").append(wrapper.isExpired(now))
                    .append("\n");
        }

        return info.toString();
    }

    //Очищает все истекшие подключения, вызывается каждые 10 минут
    @Scheduled(fixedRate = 120000)
    public void cleanupExpiredConnections() {
        LocalDateTime currentTime = LocalDateTime.now();
        Iterator<Map.Entry<String, ConnectionWrapper>> iterator = connections.entrySet().iterator();
        int removedCount = 0;

        while (iterator.hasNext()) {
            Map.Entry<String, ConnectionWrapper> entry = iterator.next();
            if (entry.getValue().isExpired(currentTime)) {
                iterator.remove();
                removedCount++;
                System.out.println("Expired connection removed for IP: " + entry.getKey());
            }
        }

        if (removedCount > 0) {
            System.out.println("Cleanup completed. Removed " + removedCount + " expired connections.");
        }
    }

    //Возвращает количество активных соединений
    public int getActiveConnectionsCount() {
        return connections.size();
    }

    //Проверяет наличие активного подключения.
    public boolean hasActiveConnection(String ip) {
        System.out.println("IP: " + ip);
        ConnectionWrapper wrapper = connections.get(ip);
        return wrapper != null && !wrapper.isExpired();
    }
}
