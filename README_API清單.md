# API 清單

所有前端請求使用 Gateway：http://localhost:8080

## Auth Service

- GET /api/auth/captcha
- POST /api/auth/login
- GET /api/auth/me
- POST /api/auth/logout
- GET /api/auth/.well-known/jwks.json
- GET /internal/auth/login-logs（ADMIN，服務間使用）

## Product Service

- GET /api/products
- GET /api/products/{id}
- GET /api/categories
- GET /api/admin/products（ADMIN）
- POST /api/admin/products（ADMIN）
- DELETE /api/admin/products/{id}（ADMIN）
- POST /api/admin/categories（ADMIN）
- PUT /api/admin/categories/{id}（ADMIN）
- DELETE /api/admin/categories/{id}（ADMIN）

## Order Service

- GET /api/cart
- POST /api/cart/add/{productId}
- POST /api/cart/increase/{cartItemId}
- POST /api/cart/decrease/{cartItemId}
- DELETE /api/cart/{cartItemId}
- DELETE /api/cart/clear
- POST /api/orders/checkout
- GET /api/orders
- GET /api/orders/{id}
- PUT /api/orders/{id}/cancel

## Report Service

- GET /api/reports/products/base64（ADMIN）
- GET /api/reports/login-history/base64（ADMIN）
- POST `/api/admin/products/import/base64`：管理員上傳 Base64 CSV／Excel／PDF，Controller 解析為 Collection，Service 以 `@Transactional` 整批寫入

### Base64 CSV 匯入流程

1. React 使用 `FileReader.readAsDataURL()` 將 CSV、Excel 或 PDF 轉為 Base64。
2. `ProductController` 接收檔名與 Base64。
3. `ProductCsvParser` 解碼並解析為 `List<ProductRequest>`。
4. `ProductService.importProducts()` 驗證資料並呼叫目前啟用的 JPA／MyBatis DAO。
5. 全部成功時交易 Commit；任何一列丟出 RuntimeException 時整批 Rollback。

CSV 標題必須是：

```csv
name,price,description,imageUrl,productUrl,categoryId
```

範例檔位於 `database/products-import-example.csv`。

Excel `.xlsx` 的第一個工作表也必須使用相同的六個欄位。PDF 必須是可搜尋文字的 PDF，標題與每筆商品使用 `|` 分隔，例如：

```text
name|price|description|imageUrl|productUrl|categoryId
PDF商品A|880|PDF匯入測試|/images/tshirt.svg||1
```

掃描圖片型 PDF 未包含文字層，必須先經過 OCR 才能解析。

## Order Report

- GET `/api/reports/orders/base64`：僅限 ADMIN，Report Service 攜帶 JWT 呼叫 Order Service 的 `/internal/orders/all`，再用 JasperReports 產生 Base64 PDF。
- PDF 欄位：訂單編號、帳號、成立時間、總金額、狀態、商品總數。
