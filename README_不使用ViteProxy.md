# StyleShop：不使用 Vite Proxy 版本

本版本的 React 會直接呼叫 Spring Boot：

React：
http://localhost:5173

Spring Boot：
http://localhost:8080

MySQL：
localhost:3306 / company_shop

## 架構

React
↓ fetch("http://localhost:8080/api/...")
Spring Boot Controller
↓
Service
↓
MyBatis Mapper.java
↓
Mapper XML
↓
MySQL

## 為什麼需要 CORS

5173 與 8080 是不同 Origin，所以 Spring Boot 已在：

src/main/java/com/example/vscodeshop/config/CorsConfig.java

允許：

http://localhost:5173

並開啟 allowCredentials(true)。

React 的 fetch 也使用：

credentials: "include"

用來保留 HttpSession 登入狀態。

## 啟動

1. 先確認 MySQL 已啟動。
2. 修改：
   src/main/resources/application.properties

   spring.datasource.password=你的MySQL密碼

3. 專案根目錄：
   mvn spring-boot:run

4. 確認：
   http://localhost:8080/api/health
   http://localhost:8080/api/products

5. 第二個 Terminal：
   cd frontend
   npm install
   npm run dev

6. 開啟：
   http://localhost:5173

## 本版本保留

- React 前端
- Spring Boot
- MyBatis
- XML SQL
- MySQL
- HttpSession 登入
- 6 位數驗證碼
- 商品列表
- 商品詳細
- 購物車
- 訂單
- 商品管理
- 種類管理
- 上一頁 / 重新整理 / 回到首頁
- 前面修正的購物車與商品管理防空白處理

## 本版本不使用

- Vite Proxy

frontend/vite.config.js 只保留 React plugin 與 5173 port。
