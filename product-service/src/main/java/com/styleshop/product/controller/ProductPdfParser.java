package com.styleshop.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.styleshop.product.service.ProductService.ProductRequest;

@Component
public class ProductPdfParser {

    private final ProductCsvParser tableParser;

    public ProductPdfParser(ProductCsvParser tableParser) {
        this.tableParser = tableParser;
    }

    public List<ProductRequest> parse(byte[] bytes) {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            String text = new PDFTextStripper().getText(document);
            List<List<String>> rows = new ArrayList<>();
            for (String line : text.split("\\R")) {
                if (line.contains("|")) {
                    rows.add(Arrays.stream(line.split("\\|", -1)).map(String::trim).toList());
                }
            }
            return tableParser.parseTable(rows, "PDF");
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("PDF 無法解析；請使用可搜尋文字、以 | 分隔欄位的 PDF");
        }
    }
}
