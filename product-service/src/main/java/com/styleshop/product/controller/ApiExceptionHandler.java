package com.styleshop.product.controller;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {@ExceptionHandler(NoSuchElementException.class)ResponseEntity<?> notFound(NoSuchElementException e){return ResponseEntity.status(404).body(Map.of("message",e.getMessage()==null?"資料不存在":e.getMessage()));}@ExceptionHandler(IllegalArgumentException.class)ResponseEntity<?> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
