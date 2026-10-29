package com.styleshop.report.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.styleshop.report.service.JasperService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final String REPORT_VERSION = "2026-09-16-bilingual-v4-embedded-font";

    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final RestClient http;
    private final JasperService jasper;

    public ReportController(RestClient http, JasperService jasper) {
        this.http = http;
        this.jasper = jasper;
    }

    @GetMapping("/products/base64")
    public Map<String, String> products(@RequestParam(defaultValue = "zh-TW") String lang) throws Exception {
        boolean english = isEnglish(lang);
        Map<?, ?> data = http.get()
                .uri("http://localhost:8082/api/products?page=0&size=1000&sortBy=id&direction=asc")
                .retrieve()
                .body(Map.class);

        List<?> categoryData = http.get()
                .uri("http://localhost:8082/api/categories")
                .retrieve()
                .body(List.class);

        Map<String, String> categoryNames = new LinkedHashMap<>();
        if (categoryData != null) {
            for (Object value : categoryData) {
                Map<?, ?> category = (Map<?, ?>) value;
                categoryNames.put(String.valueOf(category.get("id")), String.valueOf(category.get("name")));
            }
        }

        List<Map<String, ?>> rows = new ArrayList<>();
        List<?> products = data != null && data.get("content") instanceof List<?> content
                ? content : List.of();

        for (Object value : products) {
            Map<?, ?> product = (Map<?, ?>) value;
            String categoryName = text(product.get("categoryName"));
            if (categoryName.isBlank()) {
                categoryName = categoryNames.getOrDefault(String.valueOf(product.get("categoryId")), "未分類");
            }

            rows.add(row(
                    "id", text(product.get("id")),
                    "name", text(product.get("name")),
                    "category", english ? translateCategoryToEnglish(categoryName) : categoryName,
                    "price", formatPrice(product.get("price"), english)));
        }

        return out(english ? "styleshop-products-en.pdf" : "styleshop-products-zh-TW.pdf", jasper.pdf(
                english ? "StyleShop Product Report" : "StyleShop 商品清單",
                List.of("id", "name", "category", "price"),
                english
                        ? List.of("Product ID", "Product Name", "Category", "Price")
                        : List.of("商品編號", "商品名稱", "商品種類", "商品價格"),
                rows, !english));
    }

    @GetMapping("/login-history/base64")
    public Map<String, String> logs(HttpServletRequest request,
            @RequestParam(defaultValue = "zh-TW") String lang) throws Exception {
        boolean english = isEnglish(lang);
        String auth = request.getHeader("Authorization");
        List<?> data = http.get().uri("http://localhost:8081/internal/auth/login-logs")
                .header("Authorization", auth).retrieve().body(List.class);
        List<Map<String, ?>> rows = new ArrayList<>();
        for (Object value : data == null ? List.of() : data) {
            Map<?, ?> log = (Map<?, ?>) value;
            rows.add(row(
                    "username", log.get("username"),
                    "time", formatDateTime(log.get("loginTime")),
                    "status", english ? text(log.get("status")) : translateLoginStatus(log.get("status")),
                    "message", english ? text(log.get("message")) : translateLoginMessage(log.get("message"))));
        }
        return out(english ? "styleshop-login-history-en.pdf" : "styleshop-login-history-zh-TW.pdf",
                jasper.pdf(english ? "StyleShop Login History" : "StyleShop 登入紀錄",
                List.of("username", "time", "status", "message"),
                english
                        ? List.of("Username", "Login Time", "Result", "Description")
                        : List.of("登入帳號", "登入時間", "登入結果", "結果說明"), rows, !english));
    }

    @GetMapping("/orders/base64")
    public Map<String, String> orders(HttpServletRequest request,
            @RequestParam(defaultValue = "zh-TW") String lang) throws Exception {
        boolean english = isEnglish(lang);
        String auth = request.getHeader("Authorization");
        List<?> data = http.get().uri("http://localhost:8083/internal/orders/all")
                .header("Authorization", auth).retrieve().body(List.class);
        List<Map<String, ?>> rows = new ArrayList<>();
        for (Object value : data == null ? List.of() : data) {
            Map<?, ?> order = (Map<?, ?>) value;
            List<?> items = order.get("items") instanceof List<?> list ? list : List.of();
            int quantity = 0;
            for (Object itemValue : items) {
                Map<?, ?> item = (Map<?, ?>) itemValue;
                if (item.get("quantity") instanceof Number number) quantity += number.intValue();
            }
            rows.add(row(
                    "id", order.get("id"),
                    "username", order.get("username"),
                    "time", formatDateTime(order.get("createdAt")),
                    "amount", formatPrice(order.get("totalAmount"), english),
                    "status", english ? text(order.get("status")) : translateOrderStatus(order.get("status")),
                    "quantity", quantity + (english ? " item(s)" : " 件")));
        }
        return out(english ? "styleshop-order-history-en.pdf" : "styleshop-order-history-zh-TW.pdf",
                jasper.pdf(english ? "StyleShop Order History" : "StyleShop 訂單紀錄",
                List.of("id", "username", "time", "amount", "status", "quantity"),
                english
                        ? List.of("Order ID", "Username", "Created At", "Amount", "Status", "Items")
                        : List.of("訂單編號", "訂購帳號", "建立時間", "訂單金額", "訂單狀態", "商品件數"), rows, !english));
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("version", REPORT_VERSION, "languages", "zh-TW,en");
    }

    private String formatDateTime(Object value) {
        String source = text(value);
        if (source.isBlank()) return "";
        try {
            return LocalDateTime.parse(source).format(DISPLAY_TIME);
        } catch (RuntimeException exception) {
            return source.replace('T', ' ');
        }
    }

    private String translateLoginStatus(Object value) {
        return switch (text(value).toUpperCase()) {
            case "SUCCESS" -> "成功";
            case "FAIL", "FAILED", "FAILURE" -> "失敗";
            default -> text(value);
        };
    }

    private String translateLoginMessage(Object value) {
        String message = text(value);
        return switch (message) {
            case "Login successful", "LOGIN_SUCCESS" -> "登入成功";
            case "Invalid username or password", "LOGIN_FAILED" -> "帳號或密碼錯誤";
            default -> message;
        };
    }

    private String translateOrderStatus(Object value) {
        return switch (text(value).toUpperCase()) {
            case "CREATED" -> "訂單成立";
            case "CANCELLED", "CANCELED" -> "已取消";
            case "COMPLETED" -> "已完成";
            default -> text(value);
        };
    }

    private String formatPrice(Object value, boolean english) {
        if (value == null) return "NT$ 0";
        try {
            return "NT$ " + new DecimalFormat("#,##0").format(new BigDecimal(String.valueOf(value)));
        } catch (NumberFormatException exception) {
            return "NT$ " + String.valueOf(value);
        }
    }

    private boolean isEnglish(String lang) {
        return lang != null && lang.toLowerCase().startsWith("en");
    }

    private String translateCategoryToEnglish(String value) {
        return switch (value) {
            case "衣服" -> "Clothing";
            case "鞋子" -> "Shoes";
            case "帽子" -> "Hats";
            case "包包" -> "Bags";
            case "褲子" -> "Pants";
            case "未分類" -> "Uncategorized";
            default -> value;
        };
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Map<String, ?> row(Object... values) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            row.put(String.valueOf(values[index]), text(values[index + 1]));
        }
        return row;
    }

    private Map<String, String> out(String name, byte[] pdf) {
        return Map.of("fileName", name, "contentType", "application/pdf",
                "base64", Base64.getEncoder().encodeToString(pdf));
    }
}
