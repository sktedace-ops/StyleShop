# 商品 PDF：種類固定順序 + 同種類價格排序

本版維持原本的 JasperReports 表格格式、繁體中文顯示與首頁「褲子」分類。

商品 PDF 排序規則：

1. 衣服
2. 鞋子
3. 帽子
4. 包包
5. 褲子

同一種類內再依價格由低到高排序。
若價格相同，再依商品編號由小到大排序。

欄位不變：

編號 → 種類 → 價格 → 商品 → 名稱

修改位置：

`src/main/java/com/example/vscodeshop/service/JasperReportService.java`

測試：

```powershell
mvn clean
mvn spring-boot:run
```

開啟：

`http://localhost:8080/api/reports/products.pdf`

注意：排序只影響 PDF 顯示，不會修改 MySQL 商品資料。
