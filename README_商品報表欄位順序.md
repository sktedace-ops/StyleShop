# 商品報表欄位順序版

商品 PDF 已調整為：

編號 → 種類 → 價格 → 商品 → 名稱

並在 JasperReportService 產生 PDF 前依商品 id 由小到大排序。
MyBatis ProductMapper.xml 原本也保留 `ORDER BY p.id`。

測試：`http://localhost:8080/api/reports/products.pdf`
