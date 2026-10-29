# Repository 擴充 DAO 層版本

本版將資料存取架構改成：

React
→ Controller
→ Service
→ Repository
→ DAO contract
→ MyBatis Mapper
→ Mapper.xml
→ MySQL

## 設計方式

`dao/*.java`
- 定義資料存取方法的 DAO 介面。

`repository/*Repository.java`
- Repository 介面 `extends` 對應 DAO 介面。

`repository/*RepositoryImpl.java`
- 使用 `@Repository`
- 實作 Repository
- 內部委派給 MyBatis Mapper。

因此 Service 不再直接注入 Mapper。

已套用：
- Product
- Category
- User
- Cart
- Order
- LoginLog

JasperReportService 的商品清單也改由 ProductRepository 取得資料。

## MyBatis XML 保留

Mapper.java 與 `src/main/resources/mappers/*.xml` 都保留，
RepositoryImpl 負責呼叫 Mapper。

## JasperReports

- `/api/reports/login-history.pdf`
- `/api/reports/products.pdf`

同時修正 JasperReports JRXML 的 `pageFooter` / `noData` 區塊順序。
