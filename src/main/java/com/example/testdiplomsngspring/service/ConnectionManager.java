package com.example.testdiplomsngspring.service;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
//@Service
public class ConnectionManager {
	private final Map<String, Connection/* (jdbc template) */> connections = new ConcurrentHashMap<>();
	//ip key jdbc template
    //1)создать
    //2)взять и если там нету то создать
    //3)создать обертку где будет храниться jdbc template и время создания
	//4)метод который будет продлевать жизнь 
	//5)метод который удаляет > N time
    public void setConnection(String ip, Connection connection) {
        connections.put(ip, connection);
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
