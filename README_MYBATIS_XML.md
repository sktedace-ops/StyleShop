# StyleShop：React + Spring Boot + MyBatis XML + MySQL

本版本已將後端主要資料存取層改成 **MyBatis Mapper + XML SQL**。

## 新架構

```text
React 前端 (localhost:5173)
        ↓ REST API / JSON
Spring Boot Controller
        ↓
Service
        ↓
Mapper 介面
        ↓
Mapper XML（SQL 寫在 XML）
        ↓
MySQL company_shop
```

## MyBatis 檔案位置

Java Mapper：

```text
src/main/java/com/example/vscodeshop/mapper/
├─ ProductMapper.java
├─ CategoryMapper.java
├─ UserMapper.java
├─ CartMapper.java
└─ OrderMapper.java
```

XML SQL：

```text
src/main/resources/mappers/
├─ ProductMapper.xml
├─ CategoryMapper.xml
├─ UserMapper.xml
├─ CartMapper.xml
└─ OrderMapper.xml
```

## 哪些功能已改用 MyBatis XML

- 商品查詢 / 依種類查詢 / 新增 / 修改 / 刪除
- 商品種類查詢 / 新增 / 修改 / 刪除
- 會員登入查詢 / 預設帳號建立
- 購物車查詢 / 加入 / 數量增減 / 移除 / 清空
- 訂單查詢 / 建立訂單 / 建立訂單明細

原本的 `repository`（Spring Data JPA Repository）已移除。JPA starter 與 Entity 註解目前保留，只用來讓 Hibernate 依既有 Entity 自動維護資料表結構；實際網站 CRUD 查詢已走 MyBatis XML。

## MyBatis 設定

`application.properties`：

```properties
mybatis.mapper-locations=classpath:mappers/*.xml
mybatis.type-aliases-package=com.example.vscodeshop.model
mybatis.configuration.map-underscore-to-camel-case=true
```

主程式使用：

```java
@MapperScan("com.example.vscodeshop.mapper")
```

## XML 寫法範例

```xml
<select id="findByCategoryId" resultMap="ProductResultMap">
    SELECT p.id, p.name, p.price, p.image_url,
           c.id AS category_id, c.name AS category_name
    FROM products p
    LEFT JOIN categories c ON c.id = p.category_id
    WHERE p.category_id = #{categoryId}
    ORDER BY p.id
</select>
```

對應 Java Mapper：

```java
List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
```

Service 呼叫 Mapper：

```java
public List<Product> getProductsByCategory(Long categoryId) {
    return productMapper.findByCategoryId(categoryId);
}
```

## 啟動方式

Terminal 1（後端）：

```powershell
mvn spring-boot:run
```

Terminal 2（React）：

```powershell
cd frontend
npm install
npm run dev
```

瀏覽器：

```text
React 網站：http://localhost:5173
商品 API：http://localhost:8080/api/products
```

## 注意

請先確認 `src/main/resources/application.properties` 的 MySQL 密碼已改成你自己的密碼。


## 購物車空白修正
- React Cart 加入 loading / empty / error 狀態，API 異常不再整頁空白。
- MyBatis CartMapper 仍透過 XML JOIN products/categories。
- Cart API 改用 LinkedHashMap，避免 nullable 欄位造成 Map.of 例外。
- CartService 加入 null-safe 金額與數量計算。

## 商品管理畫面修正
- 商品管理加入 loading / empty / error 狀態，API 異常時不再顯示整頁空白。
- 商品價格顯示改為 Number(...) 防止 React toLocaleString 錯誤。
- 後端商品管理 API 對 null 欄位加入防呆。
- 新增與修改商品會驗證名稱、價格、種類。
- 商品 CRUD 繼續由 ProductMapper.java + ProductMapper.xml 操作 MySQL。

## 導航修正
- 購物車、訂單、商品詳細、登入、商品管理、種類管理頁面新增「上一頁 / 重新整理 / 回到首頁」。
- Header 的「首頁」改為 React Router 導航，不需要再用瀏覽器上一頁後重新整理。
- 登出後自動回到 React 首頁。
