# JasperReports 7.0.1 繁體中文 PDF 修正

本版針對 PDF 中「中文變空白」做修正。

PDF 字型映射：
- pdfFontName = MSung-Light
- pdfEncoding = UniCNS-UCS2-H
- pdfEmbedded = false

已套用：
- product-list-report.jrxml
- login-history-report.jrxml
- test-report.jrxml
- jasperreports.properties 預設 PDF 字型設定

商品清單仍維持：
編號 → 種類 → 價格 → 商品 → 名稱
並依商品 ID 由小到大排列。

啟動後測試：
http://localhost:8080/api/reports/products.pdf
