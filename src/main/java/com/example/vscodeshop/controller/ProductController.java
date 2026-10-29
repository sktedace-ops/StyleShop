package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping
    public List<Map<String,Object>> getProducts(@RequestParam(required=false) Long categoryId) {
        List<Product> products = categoryId == null ? productService.getAllProducts() : productService.getProductsByCategory(categoryId);
        List<Map<String,Object>> result = new ArrayList<>();
        for (Product p : products) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",p.getId()); m.put("name",p.getName()); m.put("price",p.getPrice());
            m.put("imageUrl",p.getImageUrl());
            m.put("categoryName",p.getCategory()==null?"未分類":p.getCategory().getName());
            result.add(m);
        }
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginUser") == null)
            return ResponseEntity.status(401).body(Map.of("message","請先登入才能查看商品詳細資料"));
        Product p = productService.getProductById(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",p.getId()); m.put("name",p.getName()); m.put("price",p.getPrice());
        m.put("imageUrl",p.getImageUrl()); m.put("productUrl",p.getProductUrl());
        m.put("description",p.getDescription());
        m.put("categoryId",p.getCategory()==null?null:p.getCategory().getId());
        m.put("categoryName",p.getCategory()==null?"未分類":p.getCategory().getName());
        return ResponseEntity.ok(m);
    }
}
