package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.model.OrderItem;
import com.example.vscodeshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class ReactOrderApiController {
    private final OrderService orderService;
    public ReactOrderApiController(OrderService orderService) { this.orderService = orderService; }

    private String user(HttpSession s) {
        Object u = s.getAttribute("loginUser");
        return u == null ? null : u.toString();
    }

    private Map<String,Object> dto(Order o) {
        List<Map<String,Object>> items = new ArrayList<>();
        int totalQty = 0;
        for (OrderItem i : o.getItems()) {
            totalQty += i.getQuantity();
            items.add(Map.of(
                "id", i.getId(),
                "productId", i.getProductId(),
                "productName", i.getProductName(),
                "imageUrl", i.getImageUrl() == null ? "" : i.getImageUrl(),
                "price", i.getPrice(),
                "quantity", i.getQuantity(),
                "subtotal", i.getSubtotal()
            ));
        }
        Map<String,Object> m = new LinkedHashMap<>();
        int subtotal = o.getTotalAmount() == null ? 0 : o.getTotalAmount();
        int shippingFee = 0;
        int discountAmount = 0;
        int finalAmount = subtotal + shippingFee - discountAmount;

        m.put("id", o.getId());
        m.put("createdAt", o.getCreatedAt());
        m.put("totalAmount", finalAmount);
        m.put("subtotal", subtotal);
        m.put("shippingFee", shippingFee);
        m.put("discountAmount", discountAmount);
        m.put("finalAmount", finalAmount);
        m.put("status", o.getStatus());
        m.put("totalQuantity", totalQty);
        m.put("items", items);
        return m;
    }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message","請先登入"));
        return ResponseEntity.ok(orderService.getOrders(username).stream().map(this::dto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message","請先登入"));
        return orderService.getOrder(id, username)
            .<ResponseEntity<?>>map(o -> ResponseEntity.ok(dto(o)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message","請先登入"));
        Order order = orderService.createOrderFromCart(username);
        if (order == null) return ResponseEntity.badRequest().body(Map.of("message","購物車目前沒有商品"));
        return ResponseEntity.ok(dto(order));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message","請先登入"));

        Optional<Order> existing = orderService.getOrder(id, username);
        if (existing.isEmpty()) return ResponseEntity.status(404).body(Map.of("message","找不到此訂單"));
        if ("已取消".equals(existing.get().getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message","此訂單已經取消"));
        }
        if (!"訂單成立".equals(existing.get().getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message","目前訂單狀態無法取消"));
        }

        boolean ok = orderService.cancelOrder(id, username);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("message","取消訂單失敗"));
        return ResponseEntity.ok(Map.of("message","訂單已取消"));
    }

}