# StyleShop｜Java Spring Boot 商品商城專題

> 個人學習與作品展示專案，重點在 Java Web 後端、資料庫存取、前後端串接及報表功能。本專案不是正式營運的購物網站。

[![Java](https://img.shields.io/badge/Java-17%2F21-orange)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-Vite-blue)](https://react.dev/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-blue)](https://www.mysql.com/)

## 專題介紹

StyleShop 是以 **Java Spring Boot** 為核心的商品商城練習專題，整合 **React + Vite** 前端、Spring MVC / Thymeleaf、**MyBatis / JPA**、**MySQL** 與 **JasperReports**。透過商品管理、會員登入、購物車、訂單與 PDF 報表等情境，練習完整的網站資料處理流程。

**原始碼：** https://github.com/sktedace-ops/StyleShop

## 主要功能

- **會員系統：** 登入、登出、登入紀錄及測試帳號。
- **商品與分類：** 商品列表、商品明細、分類篩選與商品維護。
- **購物車：** 加入商品、調整數量、清空購物車。
- **訂單：** 建立訂單、查看訂單與取消訂單。
- **管理功能：** 商品及種類管理。
- **PDF 報表：** 使用 JasperReports 匯出商品清單與登入紀錄。

> 功能依專案既有文件整理；不同前端頁面可能提供不同操作入口，請以實際執行結果為準。

## 使用技術

| 類別 | 技術 | 用途 |
| --- | --- | --- |
| 後端 | Java、Spring Boot 3.3.5、Spring MVC | API 與網站邏輯 |
| 前端 | React、Vite、JavaScript | 互動介面與 API 串接 |
| 伺服器端頁面 | Thymeleaf | Spring MVC 頁面 |
| 資料存取 | MyBatis、Mapper XML、JPA | 資料查詢及持久化 |
| 資料庫 | MySQL | 儲存會員、商品、訂單等資料 |
| 報表 | JasperReports 7.0.1 | PDF 報表 |
| 建置 | Maven | Java 依賴與啟動 |
| 開發工具 | VS Code、Git、GitHub | 開發及版本管理 |

## 系統架構與資料流程

```text
使用者瀏覽器
  ├─ React + Vite (開發環境通常為 localhost:5173)
  │    └─ fetch / HTTP / JSON
  │         └─ Spring Boot REST API (localhost:8080)
  └─ Spring MVC + Thymeleaf (localhost:8080)
                │
           Controller
                │
             Service
                │
           Repository / DAO
                │
          MyBatis / JPA
                │
              MySQL

報表：Controller → JasperReportService → 資料查詢 → JasperReports → PDF
```

專案採分層設計，將請求處理、商業邏輯與資料庫操作分離，以便理解、維護及擴充。

## 專案目錄

```text
StyleShop/
├─ frontend/                  # React 前端
├─ src/
│  └─ main/
│     ├─ java/
│     │  └─ com/example/vscodeshop/
│     │     ├─ controller/   # 控制器 / API
│     │     ├─ config/       # Spring 設定
│     │     └─ ...           # 其他後端模組
│     └─ resources/
│        ├─ application.properties
│        └─ mappers/         # MyBatis SQL XML
├─ pom.xml                    # Maven 設定
├─ .gitignore
└─ README.md
```

> 此處為重點目錄示意，完整類別與路徑以 GitHub 原始碼為準。

## 本機啟動

### 1. 準備環境

- JDK 17 或 21
- Maven
- MySQL
- Node.js 與 npm（執行 React 前端時）

### 2. 設定 MySQL

建立專案所需的資料庫，並確認 `src/main/resources/application.properties` 的連線資訊符合本機環境。範例：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/company_shop?createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
server.port=8080
```

上方是設定範例，實際連線參數請依現有專案檔案調整。不要將真實密碼寫進 GitHub。

在 **PowerShell** 中設定目前工作階段的資料庫密碼：

```powershell
$env:DB_PASSWORD = Read-Host "請輸入本機 MySQL 密碼"
```

### 3. 啟動 Spring Boot 後端

在專案根目錄執行：

```powershell
mvn spring-boot:run
```

若看到 `Tomcat started on port 8080` 和 `Started VscodeShopApplication`，代表後端已啟動。瀏覽 http://localhost:8080 。

### 4. 啟動 React 前端

開啟另一個終端機：

```powershell
cd frontend
npm install
npm run dev
```

依終端機顯示的網址開啟前端（通常為 http://localhost:5173）。請確認前端 API 設定指向本機 Spring Boot 服務。

> **注意：** GitHub 儲存庫展示的是原始碼，不會自動將 Spring Boot + MySQL 網站部署上線。若希望別人直接操作網站，需要另外部署後端、前端與資料庫。

## 畫面展示

請在實際截圖後新增圖片，例如放在 `docs/images/`：

| 畫面 | 說明 |
| --- | --- |
| 首頁 | 商品列表及分類入口 |
| 會員登入 | 帳號登入介面 |
| 購物車 | 商品數量與購物車管理 |
| 訂單 | 訂單查詢及狀態 |
| 後台 | 商品及分類管理 |
| PDF 報表 | JasperReports 匯出結果 |

<!-- 加入截圖後可取消以下註解：
![StyleShop 首頁](docs/images/home.png)
![StyleShop 購物車](docs/images/cart.png)
-->

## 練習重點

1. 使用 Spring Boot 建立網站與 REST API。
2. 理解 Controller、Service、Repository / DAO 的職責分工。
3. 以 MyBatis Mapper XML 管理 SQL，並練習 JPA 資料存取。
4. 使用 React `fetch()` 與後端交換 JSON。
5. 使用 MySQL 處理會員、商品、購物車與訂單資料。
6. 使用 JasperReports 產生 PDF 報表。
7. 使用 Git / GitHub 進行版本管理與作品分享。

## 後續改善方向

- 增加自動化單元測試與 API 測試。
- 將測試帳號初始化限制在開發環境。
- 完善輸入驗證、錯誤處理及角色權限。
- 加入 Docker 與雲端部署流程。
- 補上操作截圖、資料庫 ER 圖及 API 文件。

## 聲明

本專案為個人學習與技術展示用途，不提供正式交易服務。範例帳號、商品與資料僅供測試；請勿在公開儲存庫中提交真實密碼、Token 或個人資料。
