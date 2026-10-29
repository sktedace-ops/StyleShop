package com.example.vscodeshop.controller;

import com.example.vscodeshop.service.JasperReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import static com.example.vscodeshop.config.SessionAuth.isAdmin;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {
    private final JasperReportService reportService;

    public ReportApiController(JasperReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/test.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> testPdf(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body(Map.of("message", "僅限管理員使用"));
        byte[] pdf = reportService.createTestPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("styleshop-jasper-test.pdf", StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
    @GetMapping(value = "/login-history.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> loginHistoryPdf(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body(Map.of("message", "僅限管理員使用"));
        byte[] pdf = reportService.createLoginHistoryPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("styleshop-login-history.pdf", StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping(value = "/products.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> productListPdf(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body(Map.of("message", "僅限管理員使用"));
        byte[] pdf = reportService.createProductListPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("styleshop-product-list.pdf", StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

}
