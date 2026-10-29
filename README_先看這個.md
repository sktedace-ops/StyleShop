# StyleShop 微服務版

React 只呼叫 API Gateway（8080）。所有 API 均由 Eclipse 中的 Spring Boot Maven 專案提供。

| 專案 | Port | 資料庫 | 責任 |
|---|---:|---|---|
| api-gateway | 8080 | 無 | 統一路由與 CORS |
| auth-service | 8081 | styleshop_auth | 帳號、驗證碼、JWT、登入紀錄 |
| product-service | 8082 | styleshop_product | 商品、種類、查詢、JPA／MyBatis |
| order-service | 8083 | styleshop_order | 購物車與訂單 |
| report-service | 8084 | 無 | JasperReports Base64 PDF |
| frontend | 5173 | 無 | React 畫面，只呼叫 Gateway |

## Eclipse 匯入

1. Eclipse 選 File > Import。
2. 選 Maven > Existing Maven Projects。
3. Root Directory 選擇 C:\Users\User\Desktop\StyleShopMicroservices。
4. 勾選根 pom 與五個子專案後按 Finish。
5. 等待 Maven 下載依賴完成。

## Docker MySQL（先啟動）

本專案的 MySQL 8.4 放在 Docker 容器 `styleshop-mysql` 執行，資料保存在 Docker Volume `styleshop_mysql_data`。第一次啟動時會執行 `database/create-databases.sql`，建立三個資料庫。

1. 安裝並啟動 Docker Desktop。
2. 在專案根目錄開啟 PowerShell。
3. 執行：

```powershell
docker compose up -d
docker compose ps
```

看到 `styleshop-mysql` 顯示 `healthy` 後，再啟動 Spring Boot。

預設帳號 root、預設密碼 1234。Spring Boot 在 Windows 主機執行，因此使用 `localhost:3306` 連到容器。需要自訂設定時，可將 `.env.example` 複製為 `.env`，或在 Eclipse 的 Run Configuration > Environment 為 auth、product、order 服務加入：

- DB_HOST=localhost
- DB_PORT=3306
- DB_USER=root
- DB_PASSWORD=你的密碼

Docker 的 `MYSQL_ROOT_PASSWORD` 必須和 Spring Boot 的 `DB_PASSWORD` 相同。

常用指令：

```powershell
docker compose ps
docker compose logs -f mysql
docker compose stop
docker compose start
```

## 啟動順序

先確認 Docker MySQL 為 healthy，再在 Eclipse 依序對 Application 類別按右鍵，選 Run As > Spring Boot App：

0. Docker MySQL（3306）
1. AuthServiceApplication（8081）
2. ProductServiceApplication（8082）
3. OrderServiceApplication（8083）
4. ReportServiceApplication（8084）
5. ApiGatewayApplication（8080）

Auth Service 必須先啟動，因為其他服務透過它的 JWK 端點取得 RSA 公鑰驗證 JWT。

## React 啟動

使用 VS Code 開啟 C:\Users\User\Desktop\StyleShopMicroservices\frontend，終端機執行：

```powershell
npm install
npm run dev
```

瀏覽器開啟 http://localhost:5173 。

## 測試帳號

- admin / admin123（ADMIN）
- user / user123（USER）
- abc123 / abc123（USER）

## DAO 切換

修改 product-service/src/main/resources/application.yml：

```yaml
styleshop:
  product-dao: mybatis
```

或改成 jpa，再重新啟動 Product Service。

## JWT 設計

Auth Service 啟動時產生 RSA 2048 金鑰。私鑰只留在 Auth Service；其他服務從 /api/auth/.well-known/jwks.json 取得公鑰。Token 使用 RS256 簽章，預設 30 分鐘到期。後端重啟後舊 Token 失效，這是本機展示設計。正式環境應使用固定的金鑰管理服務與金鑰輪替。

## 常見錯誤

- 401：Token 未提供、過期，或 Auth Service 重啟後舊 Token 已失效。
- 403：USER 呼叫 ADMIN API。
- 502：Gateway 找不到目標服務，確認 8081 至 8084 已啟動。
- MySQL 1045：`DB_PASSWORD` 與 Docker 的 `MYSQL_ROOT_PASSWORD` 不同。
- Communications link failure：先執行 `docker compose ps`，確認 `styleshop-mysql` 為 healthy，並確認 3306 未被其他 MySQL 使用。
- Port already in use：舊服務尚未停止。
