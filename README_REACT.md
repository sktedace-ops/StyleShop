# StyleShop React + Spring Boot + MySQL

## 架構
React (localhost:5173) -> REST API -> Spring Boot (localhost:8080) -> Service -> Repository -> MySQL

## 第一次安裝
1. 確認 MySQL 已啟動。
2. `src/main/resources/application.properties` 填入你的 MySQL 密碼。
3. VS Code 開啟本專案根目錄。
4. 開第一個 Terminal：
   `mvn spring-boot:run`
5. 再開第二個 Terminal：
   `cd frontend`
   `npm install`
   `npm run dev`
6. 瀏覽器開啟 `http://localhost:5173`

## 之後啟動
Terminal 1：`mvn spring-boot:run`
Terminal 2：
`cd frontend`
`npm run dev`

## 功能
- React 商品首頁、種類篩選、9 筆分頁
- React 登入/登出與 6 位數驗證碼
- 商品詳細資料（登入後）
- MySQL 購物車、數量增減、移除
- 建立訂單、訂單列表、訂單明細
- 商品新增/修改/移除
- 商品種類新增/修改/刪除
- Spring Boot HttpSession 登入狀態
