# 商品 PDF：價格由便宜到貴排序

這一版只調整「商品清單 PDF」的資料排序，原本 JasperReports 格子、欄位、中文字型設定都保留。

欄位仍為：

編號 → 種類 → 價格 → 商品 → 名稱

排序規則：

1. 價格由低到高
2. 若價格相同，再依商品編號由小到大

修改位置：

`src/main/java/com/example/vscodeshop/service/JasperReportService.java`

測試網址：

`http://localhost:8080/api/reports/products.pdf`

啟動方式：

```powershell
mvn clean
mvn spring-boot:run
```

此排序只影響 JasperReports 商品 PDF，不會修改 MySQL 裡面的商品資料。
