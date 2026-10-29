package com.example.vscodeshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_logs")
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 255)
    private String message;

    public LoginLog() {}

    public LoginLog(String username, LocalDateTime loginTime, String status, String message) {
        this.username = username;
        this.loginTime = loginTime;
        this.status = status;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // JasperReports 顯示用
    public String getResultText() {
        return "SUCCESS".equalsIgnoreCase(status) ? "登入成功" : "登入失敗";
    }
}
