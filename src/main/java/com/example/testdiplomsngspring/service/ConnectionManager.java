package com.example.testdiplomsngspring.service;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void setConnection(String userId, Connection connection) {
        connections.put(userId, connection);
    }

    public Connection getConnection(String userId) {
        return connections.get(userId);
    }

    public boolean isConnected(String userId) {
        try {
            Connection conn = connections.get(userId);
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public void disconnect(String userId) {
        Connection conn = connections.remove(userId);
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignored) {}
        }
    }
}
