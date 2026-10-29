package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String productPage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("requestedProductId", id);
            return "redirect:/login-page?required=1";
        }

        Optional<Product> product = productService.getProductById(id);

        if (product.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("product", product.get());
        return "product";
    }
}
