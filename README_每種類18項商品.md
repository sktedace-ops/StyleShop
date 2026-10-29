# 每一種類 18 項商品版本

本版本已修改 `product-service` 的 `DataInitializer`：

- 衣服、鞋子、帽子、包包、褲子會各自補到 18 項，正常結果共 90 項。
- 不會刪除現有商品，只會補足不足的數量。
- `image_url` 空白、使用 `no-image.svg`，或指向不存在的本機圖片時，會依商品種類自動修復。
- 每一種類使用自己的圖片清單；圖片數量不足 18 張時會循環使用有效圖片。
- 再次啟動不會重複新增；只有某種類少於 18 項時才會補足。

## 套用方法

1. 先關閉舊的 product-service（8082）。
2. 在 Eclipse 對 `product-service` 執行 `Maven > Update Project`。
3. 執行 `Project > Clean`。
4. 啟動 `ProductServiceApplication.java`。
5. 確認 Console 顯示 `Tomcat started on port 8082`。
6. 保持 api-gateway（8080）與 frontend（5173）運作，重新整理瀏覽器。

## 驗證

PowerShell：

```powershell
$response = Invoke-RestMethod "http://localhost:8080/api/products?page=0&size=200"
$response.totalElements
$response.content | Group-Object categoryName | Select-Object Name, Count
$response.content | Where-Object { -not $_.imageUrl }
```

在原本五種類都少於 18 項的情況下，預期總數為 `90`，每個種類的 Count 都是 `18`，最後一個缺圖查詢沒有結果。

## 商品頁的購物車與訂單按鈕

- `加入購物車`：只呼叫 `POST /api/cart/add/{productId}`，成功後留在商品頁並顯示提示，不會建立訂單。
- `加入訂單`：呼叫 `POST /api/orders/direct/{productId}`，直接建立數量 1 的獨立訂單。
- 直接加入訂單不會修改、結帳或清空原本購物車。
- 最新版點「加入購物車」後會留在商品頁，顯示「已加入購物車，尚未建立訂單」，使用者可自行點「前往購物車」。
- 登出後會立即清除 JWT、重設前端登入狀態並跳到登入頁，不需要手動重新整理。

## 訂單顯示修正

- 訂單明細與「我的訂單」清單，商品件數都改由 `items[].quantity` 加總，不再錯誤顯示 0 件。
- 後端狀態 `CREATED`、`CANCELLED` 在前端顯示為「訂單成立」、「已取消」。
- 訂單明細的「返回訂單紀錄」下方提供「取消訂單」按鈕；已取消訂單不再顯示按鈕。
- 新訂單會保存商品圖片；舊訂單沒有圖片欄位時，前端會依 `productId` 自動向商品服務補抓圖片。

## 商品清單 PDF 中文化

- 報表標題改為「StyleShop 商品清單」。
- 欄位改為「商品編號、商品名稱、商品種類、商品價格」。
- 商品種類優先讀取 `categoryName`，缺少時使用 `categoryId` 對照種類 API，最後才顯示「未分類」。
- 價格格式改成 `NT$ 1,234`。
- JasperReports 使用 Windows 微軟正黑體及 `Identity-H`，避免繁體中文字被省略。

## 所有 PDF 中文化

- 登入紀錄標題改為「StyleShop 登入紀錄」。
- 登入紀錄欄位改為「登入帳號、登入時間、登入結果、結果說明」。
- 登入狀態改為「成功／失敗」，常用訊息同步翻成中文。
- 訂單報表標題改為「StyleShop 訂單紀錄」。
- 訂單欄位改為「訂單編號、訂購帳號、建立時間、訂單金額、訂單狀態、商品件數」。
- 訂單狀態改為「訂單成立／已取消／已完成」，金額顯示 `NT$`，數量顯示「件」。
- 日期統一顯示為 `yyyy/MM/dd HH:mm:ss`。

## 商品圖片圖案化

- 所有商品 SVG 已移除 `Bag`、`CAP`、`SHOES` 等英文文字。
- 原本只有文字的包包、鞋子、上衣、帽子圖片，已改成實際商品輪廓。
- 其他商品圖片保留原有服飾形狀並移除底部文字。
- 備用圖片也改成圖示，不再顯示 `No Image` 英文。
