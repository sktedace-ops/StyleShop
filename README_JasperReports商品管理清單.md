# JasperReports 商品管理清單

新增 PDF：
`http://localhost:8080/api/reports/products.pdf`

報表欄位：
- 編號
- 商品名稱
- 分類
- 價格
- 圖片路徑
- 商品描述

資料來源：
ProductMapper.findAll() -> MyBatis XML -> MySQL -> JasperReports

React 上方也加入「商品清單 PDF」連結。
