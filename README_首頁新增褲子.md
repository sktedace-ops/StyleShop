# 首頁新增「褲子」分類

這一版在 Spring Boot 8080 首頁加入「褲子」。

修改內容：
- 上方深色導覽列：新增「褲子」
- 首頁分類捷徑：新增「👖 褲子」
- 桌面版分類方塊改為 6 欄，讓「全部商品 + 衣服 + 鞋子 + 帽子 + 包包 + 褲子」可排在同一列
- 原本 API、MyBatis、Repository/DAO、JasperReports、價格排序功能全部保留

首頁：
http://localhost:8080

測試方式：
1. mvn clean
2. mvn spring-boot:run
3. 瀏覽器開啟 http://localhost:8080

只要資料庫 categories 裡有名稱為「褲子」的分類，點擊「褲子」就會自動找到該 category id 並載入商品。
