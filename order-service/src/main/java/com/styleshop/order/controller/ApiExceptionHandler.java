package com.styleshop.order.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({ IllegalStateException.class, NoSuchElementException.class })
    ResponseEntity<?> handle(RuntimeException exception) {
        String message = exception.getMessage() == null ? "操作失敗" : exception.getMessage();
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
