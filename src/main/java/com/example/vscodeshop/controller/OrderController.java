package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getUsername(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        return loginUser == null ? null : loginUser.toString();
    }

    @GetMapping
    public String orderList(HttpSession session, Model model) {
        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?order=1";
        }

        model.addAttribute("orders", orderService.getOrders(username));
        return "orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?order=1";
        }

        Order order = orderService.getOrder(id, username).orElse(null);

        if (order == null) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order-detail";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        String username = getUsername(session);

        if (username == null) {
            return "redirect:/login-page?order=1";
        }

        Order order = orderService.createOrderFromCart(username);

        if (order == null) {
            return "redirect:/cart?empty=1";
        }

        return "redirect:/orders/" + order.getId() + "?success=1";
    }
}
