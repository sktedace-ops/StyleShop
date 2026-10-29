# 商品報表格線版

已將 `product-list-report.jrxml` 改成表格樣式：

- 標題列有外框
- 每筆商品資料都有格線
- 「分類」欄位改名為「種類」
- 「商品描述」欄位改名為「明細」
- 種類、價格、明細都各自在獨立格子中
- 保留 JasperReports 7.0.1
- 保留 Repository 擴充 DAO + MyBatis XML 架構

PDF 測試網址：
`http://localhost:8080/api/reports/products.pdf`
