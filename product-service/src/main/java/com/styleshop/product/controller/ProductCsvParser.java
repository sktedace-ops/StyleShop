package com.styleshop.product.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.styleshop.product.service.ProductService.ProductRequest;

@Component
public class ProductCsvParser {

    public List<ProductRequest> parse(byte[] bytes) {
        String csv = new String(bytes, StandardCharsets.UTF_8).replaceFirst("^\\uFEFF", "");
        List<List<String>> rows = parseRows(csv);
        return parseTable(rows, "CSV");
    }

    public List<ProductRequest> parseTable(List<List<String>> rows, String fileType) {
        if (rows.size() < 2) {
            throw new IllegalArgumentException(fileType + " 必須包含標題列和至少一筆商品");
        }

        validateHeader(rows.get(0));
        List<ProductRequest> products = new ArrayList<>();
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.stream().allMatch(String::isBlank)) {
                continue;
            }
            if (row.size() != 6) {
                throw new IllegalArgumentException(fileType + " 第 " + (index + 1) + " 列必須有 6 個欄位");
            }
            try {
                products.add(new ProductRequest(
                        null,
                        row.get(0).trim(),
                        new BigDecimal(row.get(1).trim()),
                        row.get(2).trim(),
                        row.get(3).trim(),
                        row.get(4).trim(),
                        row.get(5).isBlank() ? null : Long.valueOf(row.get(5).trim())));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(fileType + " 第 " + (index + 1) + " 列的價格或種類編號格式錯誤");
            }
        }

        if (products.isEmpty()) {
            throw new IllegalArgumentException(fileType + " 沒有可匯入的商品資料");
        }
        return products;
    }

    private void validateHeader(List<String> header) {
        List<String> expected = List.of(
                "name", "price", "description", "imageUrl", "productUrl", "categoryId");
        if (header.size() != expected.size()) {
            throw new IllegalArgumentException("CSV 標題格式錯誤，請下載系統提供的範例");
        }
        for (int index = 0; index < expected.size(); index++) {
            if (!expected.get(index).equalsIgnoreCase(header.get(index).trim())) {
                throw new IllegalArgumentException("CSV 標題格式錯誤，請下載系統提供的範例");
            }
        }
    }

    private List<List<String>> parseRows(String csv) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < csv.length(); index++) {
            char current = csv.charAt(index);
            if (current == '"') {
                if (quoted && index + 1 < csv.length() && csv.charAt(index + 1) == '"') {
                    value.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                row.add(value.toString());
                value.setLength(0);
            } else if ((current == '\n' || current == '\r') && !quoted) {
                if (current == '\r' && index + 1 < csv.length() && csv.charAt(index + 1) == '\n') {
                    index++;
                }
                row.add(value.toString());
                value.setLength(0);
                rows.add(row);
                row = new ArrayList<>();
            } else {
                value.append(current);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("CSV 引號格式錯誤");
        }
        if (!row.isEmpty() || value.length() > 0) {
            row.add(value.toString());
            rows.add(row);
        }
        return rows;
    }
}
