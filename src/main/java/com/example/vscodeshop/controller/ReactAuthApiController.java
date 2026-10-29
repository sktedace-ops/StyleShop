package com.example.vscodeshop.controller;

import com.example.vscodeshop.service.LoginService;
import com.example.vscodeshop.service.LoginLogService;
import com.example.vscodeshop.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ReactAuthApiController {
    private final LoginService loginService;
    private final LoginLogService loginLogService;
    private final SecureRandom random = new SecureRandom();

    public ReactAuthApiController(LoginService loginService, LoginLogService loginLogService) {
        this.loginService = loginService;
        this.loginLogService = loginLogService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        Object user = session.getAttribute("loginUser");
        Object role = session.getAttribute("loginRole");
        return Map.of("loggedIn", user != null,
                "username", user == null ? "" : user.toString(),
                "role", role == null ? "" : role.toString());
    }

    @GetMapping("/captcha")
    public Map<String, String> captcha(HttpSession session) {
        String code = String.valueOf(100000 + random.nextInt(900000));
        session.setAttribute("loginCaptcha", code);
        return Map.of("captcha", code);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");

        String saved = (String) session.getAttribute("loginCaptcha");
        session.removeAttribute("loginCaptcha");
        String input = body.getOrDefault("captcha", "").trim();

        if (saved == null || !saved.equals(input)) {
            loginLogService.record(username, false, "驗證碼錯誤");
            return ResponseEntity.badRequest().body(Map.of("message", "驗證碼錯誤，請重新輸入"));
        }

        User user = loginService.authenticate(username, password);
        if (user == null) {
            loginLogService.record(username, false, "帳號或密碼錯誤");
            return ResponseEntity.status(401).body(Map.of("message", "帳號或密碼錯誤"));
        }

        session.setAttribute("loginUser", username);
        session.setAttribute("loginRole", user.getRole());
        loginLogService.record(username, true, "登入成功");
        return ResponseEntity.ok(Map.of("message", "登入成功", "username", username, "role", user.getRole()));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "已登出");
    }
}
