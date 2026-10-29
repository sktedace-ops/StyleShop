# JasperReports 登入紀錄 PDF 版

這個版本保留原本 React 登入頁面，另外使用 JasperReports 產生「登入紀錄清單 PDF」。

## 為什麼不是用 JasperReports 做登入表單？
JasperReports 是報表/PDF 產生工具，不適合拿來做可輸入帳號、密碼、驗證碼的互動式登入頁面。
因此：
- React：負責真正的登入介面
- Spring Boot：處理登入 API
- MyBatis XML：儲存登入紀錄
- JasperReports：把登入紀錄輸出成 PDF

## 新增資料表
`login_logs`
- id
- username
- login_time
- status
- message

Hibernate `ddl-auto=update` 會在啟動時建立資料表。

## 紀錄時機
- 驗證碼錯誤：FAILED
- 帳號或密碼錯誤：FAILED
- 登入成功：SUCCESS

## PDF
網址：
`http://localhost:8080/api/reports/login-history.pdf`

版面採用乾淨白底、置中標題、四欄清單：
- 編號
- 登入帳號
- 登入時間
- 登入結果

## 啟動
```powershell
mvn clean
mvn spring-boot:run
```

再啟動 React：
```powershell
cd frontend
npm install
npm run dev
```
