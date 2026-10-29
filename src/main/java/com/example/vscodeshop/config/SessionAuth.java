package com.example.vscodeshop.config;

import jakarta.servlet.http.HttpSession;

public final class SessionAuth {
    private SessionAuth() {}

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginUser") != null;
    }

    public static boolean isAdmin(HttpSession session) {
        return isLoggedIn(session) && "ADMIN".equals(session.getAttribute("loginRole"));
    }
}
