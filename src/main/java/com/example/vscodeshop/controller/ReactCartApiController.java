package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.CartItem;
import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class ReactCartApiController {
    private final CartService cartService;

    public ReactCartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    private String user(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        return loginUser == null ? null : loginUser.toString();
    }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        String username = user(session);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getCartItems(username)) {
            Product product = cartItem.getProduct();
            if (product == null) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", cartItem.getId());
            item.put("productId", product.getId());
            item.put("name", product.getName() == null ? "未命名商品" : product.getName());
            item.put("price", product.getPrice() == null ? 0 : product.getPrice());
            item.put("imageUrl", product.getImageUrl() == null ? "" : product.getImageUrl());
            item.put("quantity", cartItem.getQuantity() == null ? 1 : cartItem.getQuantity());
            item.put("subtotal",
                    (product.getPrice() == null ? 0 : product.getPrice())
                    * (cartItem.getQuantity() == null ? 1 : cartItem.getQuantity()));
            items.add(item);
        }

        int subtotal = cartService.getTotal(username);
        int shippingFee = 0;
        int discountAmount = 0;
        int finalAmount = subtotal + shippingFee - discountAmount;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("total", finalAmount);
        result.put("subtotal", subtotal);
        result.put("shippingFee", shippingFee);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", finalAmount);
        result.put("totalQuantity", cartService.getTotalQuantity(username));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> add(@PathVariable Long productId, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        cartService.addProduct(username, productId);
        return ResponseEntity.ok(Map.of("message", "已加入購物車"));
    }

    @PostMapping("/increase/{id}")
    public ResponseEntity<?> increase(@PathVariable Long id, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        cartService.increase(id, username);
        return ResponseEntity.ok(Map.of("message", "數量已增加"));
    }

    @PostMapping("/decrease/{id}")
    public ResponseEntity<?> decrease(@PathVariable Long id, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        cartService.decrease(id, username);
        return ResponseEntity.ok(Map.of("message", "數量已減少"));
    }


    @DeleteMapping("/clear")
    public ResponseEntity<?> clear(HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message","請先登入"));
        cartService.clear(username);
        return ResponseEntity.ok(Map.of("message","購物車已清空"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id, HttpSession session) {
        String username = user(session);
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        cartService.remove(id, username);
        return ResponseEntity.ok(Map.of("message", "已移除商品"));
    }
}
