package com.example.testdiplomsngspring.service;

public class ConnectionRequest {
    private String ip;
    private String code;
    private String userId; // добавлено


    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
