# JasperReports 7.0.1 升級版

本專案已將 Maven dependency 升級成：

- net.sf.jasperreports:jasperreports:7.0.1
- net.sf.jasperreports:jasperreports-pdf:7.0.1

並把三份 JRXML 改成 JasperReports 7 的 Jackson XML 格式：
- reports/test-report.jrxml
- reports/login-history-report.jrxml
- reports/product-list-report.jrxml

資料架構維持：
React → Controller → Service → Repository → DAO → MyBatis Mapper → Mapper.xml → MySQL

注意：
JasperReports 7 與 6.x JRXML 不相容，因此不可把舊 6.x JRXML 原封不動放回此專案。
如果你已安裝 Jaspersoft Studio 7，也可以用 Studio 開啟/儲存這些 JRXML 再做視覺調整。
