package com.styleshop.product.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import com.styleshop.product.service.ProductService.ProductRequest;

@Component
public class ProductExcelParser {

    private final ProductCsvParser tableParser;

    public ProductExcelParser(ProductCsvParser tableParser) {
        this.tableParser = tableParser;
    }

    public List<ProductRequest> parse(byte[] bytes) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            List<List<String>> rows = new ArrayList<>();
            for (Row row : sheet) {
                List<String> values = new ArrayList<>();
                for (int column = 0; column < 6; column++) {
                    values.add(formatter.formatCellValue(
                            row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)));
                }
                rows.add(values);
            }
            return tableParser.parseTable(rows, "Excel");
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Excel 檔案無法讀取，請確認為有效的 .xlsx 檔案");
        }
    }
}
