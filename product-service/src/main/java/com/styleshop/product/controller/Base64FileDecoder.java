package com.styleshop.product.controller;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Base64FileDecoder {

    private static final int MAX_FILE_BYTES = 10 * 1024 * 1024;

    public byte[] decode(String base64Content) {
        if (base64Content == null || base64Content.isBlank()) {
            throw new IllegalArgumentException("上傳檔案不可為空");
        }
        String encoded = base64Content.contains(",")
                ? base64Content.substring(base64Content.indexOf(',') + 1)
                : base64Content;
        try {
            byte[] bytes = Base64.getDecoder().decode(encoded);
            if (bytes.length > MAX_FILE_BYTES) {
                throw new IllegalArgumentException("上傳檔案不可超過 10 MB");
            }
            return bytes;
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("10 MB")) {
                throw exception;
            }
            throw new IllegalArgumentException("檔案不是有效的 Base64 格式");
        }
    }
}
