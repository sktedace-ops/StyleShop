package com.example.vscodeshop;

import com.example.vscodeshop.repository.CategoryRepository;
import com.example.vscodeshop.repository.ProductRepository;
import com.example.vscodeshop.repository.UserRepository;
import com.example.vscodeshop.model.Category;
import com.example.vscodeshop.model.Product;
import com.example.vscodeshop.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@MapperScan("com.example.vscodeshop.mapper")
public class VscodeShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(VscodeShopApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // 建立測試帳號，並將舊版明文密碼自動轉成 BCrypt。
            createOrUpdateTestUser(userRepository, passwordEncoder,
                    "admin", "admin123", "ADMIN");
            createOrUpdateTestUser(userRepository, passwordEncoder,
                    "user", "user123", "USER");
            createOrUpdateTestUser(userRepository, passwordEncoder,
                    "abc123", "abc123", "USER");

            Category clothes = getOrCreateCategory(categoryRepository, "衣服");
            Category shoes = getOrCreateCategory(categoryRepository, "鞋子");
            Category hats = getOrCreateCategory(categoryRepository, "帽子");
            Category bags = getOrCreateCategory(categoryRepository, "包包");
            Category pants = getOrCreateCategory(categoryRepository, "褲子");

            seed(productRepository, "基本款 T-shirt", 599, "/images/tshirt-basic.svg", "舒適簡約的基本款 T-shirt。", clothes);
            seed(productRepository, "純棉短袖上衣", 499, "/images/cotton-tee.svg", "柔軟純棉材質，適合日常穿著。", clothes);
            seed(productRepository, "寬鬆休閒 T-shirt", 650, "/images/loose-tee.svg", "寬鬆版型，休閒百搭。", clothes);
            seed(productRepository, "條紋長袖上衣", 790, "/images/stripe-long.svg", "經典條紋設計，適合換季穿搭。", clothes);
            seed(productRepository, "連帽休閒上衣", 1190, "/images/hoodie.svg", "舒適連帽設計，適合秋冬。", clothes);
            seed(productRepository, "輕量防風外套", 1690, "/images/windbreaker.svg", "輕量防風，適合戶外活動。", clothes);
            seed(productRepository, "牛仔休閒外套", 1490, "/images/denim-jacket.svg", "經典牛仔造型，耐穿百搭。", clothes);
            seed(productRepository, "機能運動上衣", 890, "/images/sport-top.svg", "透氣快乾，適合運動使用。", clothes);
            seed(productRepository, "簡約針織衫", 990, "/images/knit.svg", "簡約針織質感，適合日常搭配。", clothes);

            seed(productRepository, "休閒鞋", 1299, "/images/casual-shoes.svg", "日常休閒鞋款，適合通勤與週末外出。", shoes);
            seed(productRepository, "經典帆布鞋", 990, "/images/canvas-shoes.svg", "經典低筒造型，簡單百搭。", shoes);
            seed(productRepository, "輕量慢跑鞋", 1690, "/images/running-shoes.svg", "輕量緩震設計，適合慢跑。", shoes);
            seed(productRepository, "復古運動鞋", 1890, "/images/retro-sneaker.svg", "復古鞋型搭配舒適鞋底。", shoes);
            seed(productRepository, "透氣網布鞋", 1390, "/images/mesh-shoes.svg", "網布鞋面提升透氣舒適度。", shoes);
            seed(productRepository, "城市步行鞋", 1590, "/images/walking-shoes.svg", "適合長時間步行與通勤。", shoes);
            seed(productRepository, "厚底休閒鞋", 1790, "/images/platform-shoes.svg", "厚底設計兼具造型與舒適。", shoes);
            seed(productRepository, "戶外健行鞋", 2190, "/images/hiking-shoes.svg", "止滑鞋底，適合戶外健行。", shoes);
            seed(productRepository, "簡約懶人鞋", 1090, "/images/slipon-shoes.svg", "方便穿脫，適合日常使用。", shoes);

            seed(productRepository, "經典棒球帽", 490, "/images/baseball-cap.svg", "經典帽型，適合休閒穿搭。", hats);
            seed(productRepository, "素色老帽", 450, "/images/dad-cap.svg", "低調素色設計，簡單百搭。", hats);
            seed(productRepository, "漁夫帽", 590, "/images/bucket-hat.svg", "休閒漁夫帽型，適合戶外活動。", hats);
            seed(productRepository, "遮陽機能帽", 690, "/images/sun-hat.svg", "加強遮陽效果，適合戶外使用。", hats);
            seed(productRepository, "透氣運動帽", 650, "/images/sport-cap.svg", "透氣材質，適合跑步與運動。", hats);
            seed(productRepository, "復古報童帽", 790, "/images/newsboy-cap.svg", "復古造型，提升穿搭層次。", hats);
            seed(productRepository, "針織毛帽", 550, "/images/beanie.svg", "保暖針織材質，適合秋冬。", hats);
            seed(productRepository, "戶外登山帽", 850, "/images/outdoor-hat.svg", "戶外機能設計，適合登山活動。", hats);
            seed(productRepository, "日系休閒帽", 620, "/images/japan-cap.svg", "簡約日系風格，適合日常穿搭。", hats);

            seed(productRepository, "城市後背包", 899, "/images/city-backpack.svg", "適合上班、上課與短程外出的城市後背包。", bags);
            seed(productRepository, "輕量旅行背包", 1099, "/images/travel-backpack.svg", "輕量大容量設計，適合短程旅行。", bags);
            seed(productRepository, "商務電腦後背包", 1599, "/images/business-backpack.svg", "內建筆電夾層，適合通勤與商務使用。", bags);
            seed(productRepository, "防潑水機能背包", 1399, "/images/utility-backpack.svg", "防潑水材質搭配多層收納。", bags);
            seed(productRepository, "簡約休閒後背包", 799, "/images/casual-backpack.svg", "簡約百搭造型，適合日常使用。", bags);
            seed(productRepository, "大容量登山背包", 1899, "/images/hiking-backpack.svg", "大容量多分層，適合戶外活動。", bags);
            seed(productRepository, "復古帆布後背包", 1199, "/images/canvas-backpack.svg", "復古帆布質感，兼顧造型與收納。", bags);
            seed(productRepository, "日系小型後背包", 699, "/images/mini-backpack.svg", "輕巧尺寸，適合短程外出。", bags);
            seed(productRepository, "多功能運動背包", 1299, "/images/sport-backpack.svg", "多隔層設計，適合健身與運動。", bags);

            seed(productRepository, "經典直筒牛仔褲", 1290, "/images/straight-jeans.svg", "經典直筒版型，日常百搭耐穿。", pants);
            seed(productRepository, "寬鬆工裝長褲", 1490, "/images/cargo-pants.svg", "多口袋工裝設計，兼具機能與休閒風格。", pants);
            seed(productRepository, "休閒棉質長褲", 890, "/images/cotton-pants.svg", "柔軟棉質材質，適合日常與居家穿著。", pants);
            seed(productRepository, "修身西裝長褲", 1590, "/images/slim-trousers.svg", "俐落修身剪裁，適合通勤與正式場合。", pants);
            seed(productRepository, "束口運動長褲", 990, "/images/jogger-pants.svg", "彈性舒適束口設計，適合運動與休閒。", pants);
            seed(productRepository, "寬版落地長褲", 1190, "/images/wide-pants.svg", "寬鬆落地版型，打造簡約街頭穿搭。", pants);
            seed(productRepository, "機能防潑水長褲", 1690, "/images/utility-pants.svg", "防潑水機能布料，適合戶外與旅行。", pants);
            seed(productRepository, "復古刷色牛仔褲", 1390, "/images/vintage-jeans.svg", "復古刷色設計，呈現自然休閒質感。", pants);
            seed(productRepository, "夏季輕薄短褲", 690, "/images/summer-shorts.svg", "輕薄透氣材質，適合夏季日常穿搭。", pants);
        };
    }

    /**
     * 建立測試帳號，或將既有帳號的明文密碼升級成 BCrypt。
     */
    private void createOrUpdateTestUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String defaultPassword,
            String role) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRole(role);
            userRepository.insert(user);
            return;
        }

        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        } else if (!storedPassword.startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(storedPassword));
        }

        user.setRole(role);
        userRepository.updatePasswordAndRole(user);
    }

    private Category getOrCreateCategory(CategoryRepository mapper, String name) {
        Category category = mapper.findByName(name);
        if (category == null) {
            category = new Category(name);
            mapper.insert(category);
        }
        return category;
    }

    private void seed(ProductRepository mapper, String name, Integer price,
                      String imageUrl, String description, Category category) {
        Product p = mapper.findByName(name);
        if (p == null) {
            p = new Product(null, name, price, imageUrl,
                    "https://example.com/product", description, category);
            mapper.insert(p);
        } else {
            // 同步預設商品資料，也能修正舊資料庫中的圖片路徑。
            p.setPrice(price);
            p.setImageUrl(imageUrl);
            p.setDescription(description);
            p.setCategory(category);
            if (p.getProductUrl() == null || p.getProductUrl().isBlank()) {
                    p.setProductUrl("https://example.com/product");
                }
            mapper.update(p);
        }
    }
}
