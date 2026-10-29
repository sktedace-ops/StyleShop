package com.example.vscodeshop.service;

import com.example.vscodeshop.repository.LoginLogRepository;
import com.example.vscodeshop.model.LoginLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginLogService {
    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public void record(String username, boolean success, String message) {
        String safeUsername = (username == null || username.isBlank()) ? "(空白帳號)" : username.trim();
        loginLogRepository.insert(new LoginLog(
                safeUsername,
                LocalDateTime.now(),
                success ? "SUCCESS" : "FAILED",
                message
        ));
    }

    public List<LoginLog> findAll() {
        return loginLogRepository.findAll();
    }
}
