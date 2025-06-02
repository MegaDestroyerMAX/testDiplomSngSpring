package com.example.testdiplomsngspring.service;

import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.time.Duration;


public class ConnectionWrapper {
    private final JdbcTemplate jdbcTemplate;
    private LocalDateTime lastAccessTime;
    private static final long EXPIRATION_TIME_MS = 5 * 60 * 1000;

    public ConnectionWrapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.lastAccessTime = LocalDateTime.now();
        System.out.println("Wrapper: Creating new connection wrapper at " + this.lastAccessTime);
    }

    //Получается JdbcTemplate для выполнения запросов
    public JdbcTemplate getJdbcTemplate() {
        extendLife();
        return jdbcTemplate;
    }

    //Продлевает время жизни соединения до текущего момента
    public void extendLife() {
        LocalDateTime oldTime = this.lastAccessTime;
        this.lastAccessTime = LocalDateTime.now();
        System.out.println("WRAPPER: Life extended from " + oldTime + " to " + this.lastAccessTime);
    }

    //Проверяет, истекло ли время жизни соединения
    public boolean isExpired() {
        return isExpired(LocalDateTime.now());
    }

    //Проверяет, истекло ли время жизни соединения на указанное время
    public boolean isExpired(LocalDateTime currentTime) {
        long timeDiff = Duration.between(lastAccessTime, currentTime).toMillis();
        boolean expired = timeDiff > EXPIRATION_TIME_MS;

        if (expired) {
            System.out.println("WRAPPER: Connection EXPIRED. Last access: " + lastAccessTime + ", Current: " + currentTime + ", Diff: " + timeDiff + "ms");
        }
        return expired;
    }

    /*
     * Получает время последнего обращения к соединению
     * @return LocalDateTime последнего обращения
     */
    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    /*
     * Получает оставшееся время жизни соединения в миллисекундах
     * @return оставшееся время в мс, или 0 если истекло
     */
    public long getRemainingTimeMs() {
        long elapsed = Duration.between(lastAccessTime, LocalDateTime.now()).toMillis();
        long remaining = EXPIRATION_TIME_MS - elapsed;
        return Math.max(0, remaining);
    }

    /*
     * Получает информацию о состоянии соединения
     * @return строка с подробной информацией
     */
    public String getConnectionInfo() {
        LocalDateTime now = LocalDateTime.now();
        long elapsedMs = Duration.between(lastAccessTime, now).toMillis();
        long remainingMs = getRemainingTimeMs();

        return String.format("Last access: %s, Elapsed: %dms, Remaining: %dms, Expired: %b",
                lastAccessTime, elapsedMs, remainingMs, isExpired(now));
    }
}
