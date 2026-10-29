package com.example.vscodeshop.controller;

import com.example.vscodeshop.service.CartService;
import com.example.vscodeshop.service.LoginService;
import com.example.vscodeshop.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;

@Controller
public class LoginController {

    private final LoginService loginService;
    private final CartService cartService;
    private final SecureRandom secureRandom = new SecureRandom();

    public LoginController(LoginService loginService, CartService cartService) {
        this.loginService = loginService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home() {
        return "list";
    }

    @GetMapping("/login-page")
    public String loginPage(HttpSession session, Model model) {
        String captcha = generateCaptcha();

        session.setAttribute("loginCaptcha", captcha);
        model.addAttribute("captcha", captcha);

        return "login";
    }

    @GetMapping("/captcha/refresh")
    public String refreshCaptcha(HttpSession session) {
        session.setAttribute("loginCaptcha", generateCaptcha());
        return "redirect:/login-page";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String captchaInput,
            HttpSession session) {

        Object savedCaptchaObject = session.getAttribute("loginCaptcha");
        String savedCaptcha = savedCaptchaObject == null
                ? null
                : savedCaptchaObject.toString();

        // 驗證碼只能使用一次
        session.removeAttribute("loginCaptcha");

        if (savedCaptcha == null || !savedCaptcha.equals(captchaInput.trim())) {
            return "redirect:/login-page?captchaError=1";
        }

        User user = loginService.authenticate(username, password);
        if (user != null) {
            session.setAttribute("loginUser", username);
            session.setAttribute("loginRole", user.getRole());

            Object requestedCartProductId =
                    session.getAttribute("requestedCartProductId");

            if (requestedCartProductId != null) {
                session.removeAttribute("requestedCartProductId");

                Long productId =
                        Long.valueOf(requestedCartProductId.toString());

                cartService.addProduct(username, productId);

                return "redirect:/cart";
            }

            Object requestedProductId =
                    session.getAttribute("requestedProductId");

            if (requestedProductId != null) {
                session.removeAttribute("requestedProductId");
                return "redirect:/product/" + requestedProductId;
            }

            return "redirect:/";
        }

        return "redirect:/login-page?error=1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private String generateCaptcha() {
        int number = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(number);
    }
}
