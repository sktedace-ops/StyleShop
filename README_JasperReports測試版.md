# JasperReports PDF 測試版

本版新增 JasperReports 6.21.3，並保留既有 React + Spring Boot + MyBatis XML + MySQL 架構。

## 功能

- JasperReports JRXML 範本：`src/main/resources/reports/test-report.jrxml`
- PDF 服務：`JasperReportService.java`
- REST API：`GET /api/reports/test.pdf`
- React 上方管理列新增「PDF 測試報表」連結

啟動後可直接開：

`http://localhost:8080/api/reports/test.pdf`

瀏覽器會直接顯示 JasperReports 產生的 PDF。

## Maven dependency

```xml
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports</artifactId>
    <version>6.21.3</version>
</dependency>
```

此版先做 ASCII/英文測試報表，以避免 JasperReports PDF 字型沒有嵌入時的中文字型問題。確認功能正常後，再擴充成「商品報表」或「訂單明細 PDF」。
