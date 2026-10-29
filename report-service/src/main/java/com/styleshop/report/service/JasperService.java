package com.styleshop.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

@Service
public class JasperService {

    private static final String EMBEDDED_CHINESE_FONT = "Noto Sans CJK TC";

    public byte[] pdf(String title, List<String> fields, List<String> labels,
            List<Map<String, ?>> rows, boolean chinese) throws JRException {
        JasperDesign design = new JasperDesign();
        design.setName(title.replace(" ", "_"));
        design.setPageWidth(595);
        design.setPageHeight(842);
        design.setColumnWidth(515);
        design.setLeftMargin(40);
        design.setRightMargin(40);
        design.setTopMargin(40);
        design.setBottomMargin(40);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(55);
        JRDesignStaticText heading = new JRDesignStaticText();
        heading.setText(title);
        heading.setX(0);
        heading.setY(0);
        heading.setWidth(515);
        heading.setHeight(35);
        heading.setFontSize(20f);
        heading.setBold(true);
        applyPdfFont(heading, chinese);
        titleBand.addElement(heading);
        design.setTitle(titleBand);

        int columnWidth = 515 / fields.size();
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(30);

        for (int index = 0; index < fields.size(); index++) {
            JRDesignField field = new JRDesignField();
            field.setName(fields.get(index));
            field.setValueClass(String.class);
            design.addField(field);

            JRDesignStaticText header = new JRDesignStaticText();
            header.setText(labels.get(index));
            header.setX(index * columnWidth);
            header.setY(0);
            header.setWidth(columnWidth);
            header.setHeight(25);
            header.setFontSize(11f);
            header.setBold(true);
            applyPdfFont(header, chinese);
            columnHeader.addElement(header);
        }
        design.setColumnHeader(columnHeader);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(25);
        for (int index = 0; index < fields.size(); index++) {
            JRDesignTextField textField = new JRDesignTextField();
            JRDesignExpression expression = new JRDesignExpression();
            expression.setText("$F{" + fields.get(index) + "}");
            textField.setExpression(expression);
            textField.setX(index * columnWidth);
            textField.setY(0);
            textField.setWidth(columnWidth);
            textField.setHeight(23);
            textField.setFontSize(10f);
            applyPdfFont(textField, chinese);
            detail.addElement(textField);
        }
        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);
        JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(),
                new JRMapCollectionDataSource(rows));
        return JasperExportManager.exportReportToPdf(print);
    }

    /** 使用專案內附的 Noto Sans CJK TC，確保繁體中文真正嵌入 PDF。 */
    private void applyPdfFont(JRDesignTextElement element, boolean chinese) {
        // 中文版字型由 resources/jasperreports_extension.properties 註冊，
        // 並以 Identity-H 真正嵌入 PDF；英文版使用 Java 通用字型。
        element.setFontName(chinese ? EMBEDDED_CHINESE_FONT : "SansSerif");
    }
}
