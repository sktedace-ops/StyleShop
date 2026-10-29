# 購物車＋訂單總結版

此版本沿用目前架構：

React 5173 → Spring Boot REST API 8080 → Service → MyBatis Mapper.java → Mapper XML → MySQL

## 新增內容

### 購物車頁
- 商品總件數
- 商品小計
- 運費
- 折扣
- 訂單總額
- 建立訂單按鈕
- 空購物車 / 載入中 / 錯誤狀態

### 訂單列表
- 訂單編號
- 下單時間
- 訂單狀態
- 商品總件數
- 商品小計
- 實付金額

### 訂單明細
- 商品明細
- 訂單編號
- 商品件數
- 商品小計
- 運費
- 折扣
- 實付金額
- 訂單狀態

目前專題設定：運費 = 0（免運）、折扣 = 0，因此不需要新增資料庫欄位。

## 額外保留修正
ProductMapper.xml 的分類查詢使用 `#{categoryId}`，避免衣服 / 鞋子 / 帽子 / 包包查詢不到商品。

## 啟動
後端：
```powershell
mvn clean
mvn spring-boot:run
```

前端：
```powershell
cd frontend
npm install
npm run dev
```

React：
http://localhost:5173

Spring Boot API：
http://localhost:8080

## 新增取消功能
- 購物車：新增「清空購物車」，會先跳出確認視窗，再刪除目前登入者購物車的所有商品。
- 訂單：新增「取消訂單」。只有狀態為「訂單成立」且屬於目前登入者的訂單可以取消。
- 取消訂單採「保留訂單紀錄、把狀態改成已取消」的方式，不會刪除訂單與訂單商品明細。
