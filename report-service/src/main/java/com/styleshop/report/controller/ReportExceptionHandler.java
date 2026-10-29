package com.styleshop.report.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReportExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(Exception exception) {
        Throwable root = exception;
        while (root.getCause() != null) root = root.getCause();
        String detail = root.getMessage();
        if (detail == null || detail.isBlank()) detail = root.getClass().getSimpleName();
        return Map.of("message", "PDF 產生失敗：" + detail);
    }
}
