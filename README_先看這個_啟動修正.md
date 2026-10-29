# 重要：先確認後端 8080 有成功啟動

這個專案的 React 前端會在 5173，Spring Boot 後端會在 8080。

如果發生：
- 登入驗證碼空白
- 首頁商品全部消失
- 商品管理無法新增
- 購物車無法讀取

通常代表 8080 後端沒有成功啟動。

## 1. MySQL 密碼
請先打開：
`src/main/resources/application.properties`

把：
`spring.datasource.password=你的MySQL密碼`

改成你自己的 MySQL root 密碼。

## 2. 啟動後端
在專案根目錄：
`mvn spring-boot:run`

確認看到：
`Tomcat started on port 8080`
`Started VscodeShopApplication`

可以測試：
`http://localhost:8080/api/health`
`http://localhost:8080/api/products`

## 3. 啟動 React
另一個 Terminal：
`cd frontend`
`npm install`
`npm run dev`

開：
`http://localhost:5173`

## 本版修正
- React 改用 Vite Proxy 呼叫 `/api`，降低 CORS / Session 問題。
- 登入頁驗證碼 API 失敗時會直接顯示原因，不再顯示空白框。
- 首頁商品 API 失敗時會顯示「後端資料目前無法載入」。
- 商品新增/修改的 MyBatis XML category 綁定修正。
- 保留購物車、商品管理、導航修正。
