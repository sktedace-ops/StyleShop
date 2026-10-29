package com.styleshop.product.config;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.styleshop.product.model.Category;
import com.styleshop.product.model.Product;
import com.styleshop.product.repository.CategoryRepository;
import com.styleshop.product.repository.JpaProductRepository;

@Configuration
public class DataInitializer {

    private static final int PRODUCTS_PER_CATEGORY = 18;
    private static final Map<String, List<String>> CATEGORY_IMAGES = new LinkedHashMap<>();

    static {
        CATEGORY_IMAGES.put("衣服", List.of(
                "/images/tshirt-basic.svg", "/images/tshirt.svg", "/images/cotton-tee.svg",
                "/images/sport-top.svg", "/images/stripe-long.svg", "/images/loose-tee.svg",
                "/images/hoodie.svg", "/images/knit.svg", "/images/denim-jacket.svg",
                "/images/windbreaker.svg"));
        CATEGORY_IMAGES.put("鞋子", List.of(
                "/images/canvas-shoes.svg", "/images/running-shoes.svg", "/images/shoes.svg",
                "/images/retro-sneaker.svg", "/images/casual-shoes.svg", "/images/hiking-shoes.svg",
                "/images/slipon-shoes.svg", "/images/mesh-shoes.svg", "/images/platform-shoes.svg",
                "/images/walking-shoes.svg"));
        CATEGORY_IMAGES.put("帽子", List.of(
                "/images/baseball-cap.svg", "/images/bucket-hat.svg", "/images/sport-cap.svg",
                "/images/dad-cap.svg", "/images/beanie.svg", "/images/outdoor-hat.svg",
                "/images/newsboy-cap.svg", "/images/sun-hat.svg", "/images/hat.svg",
                "/images/japan-cap.svg"));
        CATEGORY_IMAGES.put("包包", List.of(
                "/images/bag.svg", "/images/casual-backpack.svg", "/images/travel-backpack.svg",
                "/images/canvas-backpack.svg", "/images/business-backpack.svg", "/images/mini-backpack.svg",
                "/images/city-backpack.svg", "/images/hiking-backpack.svg", "/images/sport-backpack.svg",
                "/images/utility-backpack.svg"));
        CATEGORY_IMAGES.put("褲子", List.of(
                "/images/cargo-pants.svg", "/images/straight-jeans.svg", "/images/wide-pants.svg",
                "/images/vintage-jeans.svg", "/images/jogger-pants.svg", "/images/utility-pants.svg",
                "/images/cotton-pants.svg", "/images/slim-trousers.svg", "/images/summer-shorts.svg"));
    }

    @Bean
    CommandLineRunner data(CategoryRepository categories, JpaProductRepository products) {
        return args -> {
            createMissingCategories(categories);
            repairMissingImages(products);
            fillEachCategory(categories, products);
        };
    }

    private void createMissingCategories(CategoryRepository categories) {
        for (String categoryName : CATEGORY_IMAGES.keySet()) {
            if (categories.findByName(categoryName).isEmpty()) {
                Category category = new Category();
                category.setName(categoryName);
                categories.save(category);
            }
        }
    }

    private void repairMissingImages(JpaProductRepository products) {
        for (Product product : products.findAll()) {
            if (!isMissingImage(product.getImageUrl())) {
                continue;
            }
            String categoryName = product.getCategory() == null ? "衣服" : product.getCategory().getName();
            List<String> images = CATEGORY_IMAGES.getOrDefault(categoryName, CATEGORY_IMAGES.get("衣服"));
            int sourceNumber = product.getId() == null ? 0 : product.getId().intValue();
            product.setImageUrl(images.get(Math.floorMod(sourceNumber, images.size())));
            products.save(product);
        }
    }

    private void fillEachCategory(CategoryRepository categories, JpaProductRepository products) {
        List<Product> allProducts = products.findAll();
        for (Map.Entry<String, List<String>> entry : CATEGORY_IMAGES.entrySet()) {
            Category category = categories.findByName(entry.getKey()).orElseThrow();
            long currentCount = allProducts.stream()
                    .filter(product -> product.getCategory() != null
                            && category.getId().equals(product.getCategory().getId()))
                    .count();

            for (long sequence = currentCount + 1; sequence <= PRODUCTS_PER_CATEGORY; sequence++) {
                Product product = new Product();
                product.setName(entry.getKey() + "精選商品 " + sequence);
                product.setPrice(priceFor(entry.getKey(), sequence));
                product.setDescription("StyleShop " + entry.getKey() + "系列，第 " + sequence + " 款精選商品");
                product.setImageUrl(entry.getValue().get((int) ((sequence - 1) % entry.getValue().size())));
                product.setCategory(category);
                products.save(product);
            }
        }
    }

    private BigDecimal priceFor(String categoryName, long sequence) {
        long startingPrice = switch (categoryName) {
            case "衣服" -> 490L;
            case "鞋子" -> 890L;
            case "帽子" -> 390L;
            case "包包" -> 790L;
            case "褲子" -> 690L;
            default -> 490L;
        };
        return BigDecimal.valueOf(startingPrice + (sequence - 1L) * 60L);
    }

    private boolean isMissingImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.endsWith("/no-image.svg")) {
            return true;
        }
        if (imageUrl.startsWith("/images/")) {
            return !new ClassPathResource("static" + imageUrl).exists();
        }
        return false;
    }
}
