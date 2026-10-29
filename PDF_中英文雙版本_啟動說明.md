# StyleShop PDF 中英文雙版本

本版提供三種報表，每一種都可分別下載中文與英文 PDF：

| 報表 | 中文版 | English version |
| --- | --- | --- |
| 商品清單 | 商品編號、商品名稱、商品種類、商品價格 | Product ID, Product Name, Category, Price |
| 登入紀錄 | 登入帳號、登入時間、登入結果、結果說明 | Username, Login Time, Result, Description |
| 訂單紀錄 | 訂單編號、訂購帳號、建立時間、訂單金額、訂單狀態、商品件數 | Order ID, Username, Created At, Amount, Status, Items |

## 為何仍看到舊的英文 PDF

如果 PDF 標題仍是 `StyleShop Product Report`，而管理頁沒有六個中英文按鈕，代表 Windows 仍在執行舊資料夾或舊的 `target/classes`，不是新版程式沒有修改。

## Eclipse 正確更新步驟

1. 在 Eclipse Console 按紅色停止鍵，停止 `ReportServiceApplication`。
2. 確認工作區中的 `report-service` 是這份新版專案內的資料夾。
3. 右鍵 `report-service` → **Maven** → **Update Project...** → 勾選 **Force Update of Snapshots/Releases** → **OK**。
4. 選單 **Project** → **Clean...** → 選 `report-service` → **Clean**。
5. 找到 `report-service/src/main/java/com/styleshop/report/ReportServiceApplication.java`。
6. 右鍵 → **Run As** → **Spring Boot App**。
7. 瀏覽器開啟 <http://localhost:8080/api/reports/version>，應顯示：

   `{"version":"2026-09-16-bilingual-v4-embedded-font","languages":"zh-TW,en"}`

8. 停止並重新啟動前端：

   ```powershell
   cd C:\Users\User\Desktop\StyleShopMicroservices\frontend
   npm run dev
   ```

9. 在瀏覽器按 `Ctrl + Shift + R` 強制重新整理。

## 可直接測試的網址

- 中文商品：`http://localhost:8080/api/reports/products/base64?lang=zh-TW`
- 英文商品：`http://localhost:8080/api/reports/products/base64?lang=en`
- 中文登入紀錄：`http://localhost:8080/api/reports/login-history/base64?lang=zh-TW`
- 英文登入紀錄：`http://localhost:8080/api/reports/login-history/base64?lang=en`
- 中文訂單：`http://localhost:8080/api/reports/orders/base64?lang=zh-TW`
- 英文訂單：`http://localhost:8080/api/reports/orders/base64?lang=en`

登入紀錄及訂單報表需要管理員 JWT，建議直接使用管理頁的下載按鈕。
