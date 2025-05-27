package com.example.testdiplomsngspring.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления подключениями пользователей через JdbcTemplate.
 * Хранит обёртки с jdbcTemplate и временем последнего обращения.
 * Удаляет неактивные соединения по таймауту.
 */
@Service
public class ConnectionManager {

    // Максимальное время жизни соединения без активности (30 минут)
    private static final long EXPIRATION_TIME_MS = 30 * 60 * 1000;

    // Карта активных соединений: ключ — IP пользователя, значение — обёртка JdbcTemplate
    private final Map<String, ConnectionWrapper> connections = new ConcurrentHashMap<>();

    /**
     * Устанавливает новое подключение для заданного IP.
     * @param ip IP-адрес пользователя
     * @param jdbcTemplate JdbcTemplate, связанный с пользователем
     */
    public void setConnection(String ip, JdbcTemplate jdbcTemplate) {
        connections.put(ip, new ConnectionWrapper(jdbcTemplate));
    }

    /**
     * Возвращает подключение (JdbcTemplate) по IP.
     * Автоматически продлевает время жизни соединения.
     * @param ip IP-адрес пользователя
     * @return JdbcTemplate или null, если не найдено
     */
    public JdbcTemplate getConnection(String ip) {
        ConnectionWrapper wrapper = connections.get(ip);
        if (wrapper != null) {
            wrapper.extendLife(); // продлеваем жизнь при доступе
            return wrapper.getJdbcTemplate();
        }
        return null;
    }

    /**
     * Проверяет, активно ли соединение для заданного IP.
     * @param ip IP-адрес пользователя
     * @return true — соединение активно и не истекло
     */
    public boolean isConnected(String ip) {
        ConnectionWrapper wrapper = connections.get(ip);
        return wrapper != null && !wrapper.isExpired();
    }

    /**
     * Отключает (удаляет) соединение пользователя.
     * @param ip IP-адрес пользователя
     */
    public void disconnect(String ip) {
        connections.remove(ip);
    }

    /**
     * Удаляет все просроченные соединения.
     * Этот метод рекомендуется вызывать по расписанию.
     */
    public void cleanupExpiredConnections() {
        Instant now = Instant.now();
        // Удаляем записи, у которых истёк срок жизни
        connections.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    /**
     * Внутренний класс-обёртка для хранения JdbcTemplate и времени последнего доступа.
     */
    private static class ConnectionWrapper {
        private final JdbcTemplate jdbcTemplate;
        private Instant lastAccessTime;

        public ConnectionWrapper(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            this.lastAccessTime = Instant.now(); // устанавливаем текущее время при создании
        }

        public JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }

        /**
         * Продлевает время жизни соединения, обновляя время последнего обращения.
         */
        public void extendLife() {
            this.lastAccessTime = Instant.now();
        }

        /**
         * Проверяет, истекло ли время жизни соединения относительно текущего времени.
         * @return true — соединение просрочено
         */
        public boolean isExpired() {
            return isExpired(Instant.now());
        }

        /**
         * Проверяет, истекло ли соединение относительно указанного времени.
         * @param currentTime Текущее время
         * @return true — соединение просрочено
         */
        public boolean isExpired(Instant currentTime) {
            return currentTime.toEpochMilli() - lastAccessTime.toEpochMilli() > EXPIRATION_TIME_MS;
        }
    }
}
