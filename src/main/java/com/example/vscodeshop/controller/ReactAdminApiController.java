package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Category;
import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.service.CategoryService;
import com.example.vscodeshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static com.example.vscodeshop.config.SessionAuth.isAdmin;

@RestController
@RequestMapping("/api/admin")
public class ReactAdminApiController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ReactAdminApiController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("message", "僅限管理員使用"));
    }

    @GetMapping("/products")
    public ResponseEntity<?> products(HttpSession session) {
        if (!isAdmin(session)) return forbidden();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Product p : productService.getAllProducts()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName() == null ? "" : p.getName());
            item.put("price", p.getPrice() == null ? 0 : p.getPrice());
            item.put("imageUrl", p.getImageUrl() == null ? "" : p.getImageUrl());
            item.put("productUrl", p.getProductUrl() == null ? "" : p.getProductUrl());
            item.put("description", p.getDescription() == null ? "" : p.getDescription());
            item.put("categoryId", p.getCategory() == null ? null : p.getCategory().getId());
            item.put("categoryName", p.getCategory() == null || p.getCategory().getName() == null
                    ? "未分類" : p.getCategory().getName());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/products")
    public ResponseEntity<?> saveProduct(@RequestBody Map<String, Object> body, HttpSession session) {
        if (!isAdmin(session)) return forbidden();

        String name = String.valueOf(body.getOrDefault("name", "")).trim();
        if (name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "商品名稱不可空白"));
        }

        Integer price;
        try {
            price = Integer.valueOf(String.valueOf(body.getOrDefault("price", "0")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "商品價格格式錯誤"));
        }
        if (price < 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "商品價格不可小於 0"));
        }

        Long id = null;
        Object idValue = body.get("id");
        if (idValue != null && !idValue.toString().isBlank() && !"null".equals(idValue.toString())) {
            try {
                id = Long.valueOf(idValue.toString());
            } catch (NumberFormatException ignored) {}
        }

        Product p = id == null ? new Product() : productService.getProductById(id).orElse(new Product());
        p.setName(name);
        p.setPrice(price);
        p.setImageUrl(safeString(body.get("imageUrl")));
        p.setProductUrl(safeString(body.get("productUrl")));
        p.setDescription(safeString(body.get("description")));

        Object categoryId = body.get("categoryId");
        if (categoryId == null || categoryId.toString().isBlank()) {
            p.setCategory(null);
        } else {
            try {
                Long cid = Long.valueOf(categoryId.toString());
                Category category = categoryService.getCategoryById(cid).orElse(null);
                if (category == null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "找不到指定的商品種類"));
                }
                p.setCategory(category);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "商品種類格式錯誤"));
            }
        }

        try {
            Product saved = productService.save(p);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "商品已儲存");
            result.put("id", saved.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "商品儲存失敗：" + e.getClass().getSimpleName()));
        }
    }

    private String safeString(Object value) {
        return value == null || "null".equals(String.valueOf(value)) ? "" : String.valueOf(value).trim();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return forbidden();
        try {
            productService.delete(id);
            return ResponseEntity.ok(Map.of("message", "商品已移除"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "商品可能仍被購物車或其他資料關聯，暫時無法刪除"));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody Map<String, String> body, HttpSession session) {
        if (!isAdmin(session)) return forbidden();
        String name = body.getOrDefault("name", "").trim();
        if (name.isBlank()) return ResponseEntity.badRequest().body(Map.of("message", "種類名稱不可空白"));
        Category c = categoryService.save(new Category(name));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "種類已新增");
        result.put("id", c.getId());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @RequestBody Map<String, String> body,
                                            HttpSession session) {
        if (!isAdmin(session)) return forbidden();
        Category c = categoryService.getCategoryById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();
        String name = body.getOrDefault("name", c.getName()).trim();
        if (name.isBlank()) return ResponseEntity.badRequest().body(Map.of("message", "種類名稱不可空白"));
        c.setName(name);
        categoryService.save(c);
        return ResponseEntity.ok(Map.of("message", "種類已更新"));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return forbidden();
        if (!productService.getProductsByCategory(id).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "此種類仍有商品，請先移動或刪除商品"));
        }
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "種類已刪除"));
    }
}
