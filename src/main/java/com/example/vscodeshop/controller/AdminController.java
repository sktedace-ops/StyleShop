package com.example.vscodeshop.controller;

import com.example.vscodeshop.model.Category;
import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.service.CategoryService;
import com.example.vscodeshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import static com.example.vscodeshop.config.SessionAuth.isAdmin;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    private boolean notAdmin(HttpSession session) {
        return !isAdmin(session);
    }

    @GetMapping
    public String adminHome(HttpSession session) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    public String productManagement(HttpSession session, Model model) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        model.addAttribute("products", productService.getAllProducts());
        return "admin-products";
    }

    @GetMapping("/products/new")
    public String newProduct(HttpSession session, Model model) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "新增商品");
        return "admin-product-form";
    }

    @GetMapping("/products/register")
    public String registerProduct(HttpSession session, Model model) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "登記商品");
        return "admin-product-form";
    }

    @GetMapping("/products/remove")
    public String removeProductPage(HttpSession session, Model model) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        model.addAttribute("products", productService.getAllProducts());
        return "admin-product-remove";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "編輯商品");
        return "admin-product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam Integer price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String productUrl,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long categoryId,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        Product product = id == null
                ? new Product()
                : productService.getProductById(id).orElse(new Product());

        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setProductUrl(productUrl);
        product.setDescription(description);

        if (categoryId != null) {
            product.setCategory(categoryService.getCategoryById(categoryId).orElse(null));
        } else {
            product.setCategory(null);
        }

        productService.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        productService.delete(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String categoryManagement(HttpSession session, Model model) {
        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin-categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(
            @RequestParam String name,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        if (name != null && !name.isBlank()) {
            Category category = new Category();
            category.setName(name.trim());
            categoryService.save(category);
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category != null && name != null && !name.isBlank()) {
            category.setName(name.trim());
            categoryService.save(category);
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/login-page?admin=1";
        }

        boolean inUse = productService.getProductsByCategory(id).size() > 0;
        if (!inUse) {
            categoryService.delete(id);
        }

        return "redirect:/admin/categories";
    }
}
