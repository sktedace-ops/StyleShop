package com.styleshop.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.styleshop.product.model.Category;
import com.styleshop.product.model.Product;
import com.styleshop.product.repository.CategoryRepository;
import com.styleshop.product.repository.ProductDataSource;

@Service
public class ProductService {

    private final ProductDataSource products;
    private final CategoryRepository categories;

    public ProductService(ProductDataSource products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    public Page<ProductDto> search(String keyword, Long categoryId, BigDecimal minPrice,
            BigDecimal maxPrice, Pageable pageable) {
        return products.search(keyword, categoryId, minPrice, maxPrice, pageable).map(ProductDto::of);
    }

    public ProductDto one(Long id) {
        return products.findById(id).map(ProductDto::of)
                .orElseThrow(() -> new NoSuchElementException("商品不存在"));
    }

    @Transactional
    public ProductDto save(ProductRequest request) {
        return ProductDto.of(saveEntity(request));
    }

    @Transactional
    public ImportResult importProducts(List<ProductRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("沒有可匯入的商品資料");
        }

        List<Long> createdIds = new ArrayList<>();
        for (int index = 0; index < requests.size(); index++) {
            try {
                Product product = saveEntity(requests.get(index));
                createdIds.add(product.getId());
            } catch (RuntimeException exception) {
                // RuntimeException 會讓 Spring 將整個交易標記為 Rollback。
                throw new IllegalArgumentException(
                        "第 " + (index + 2) + " 列匯入失敗，整批資料已 Rollback：" + exception.getMessage(),
                        exception);
            }
        }
        return new ImportResult(requests.size(), createdIds.size(), products.name(), createdIds,
                "全部成功，交易已 Commit");
    }

    private Product saveEntity(ProductRequest request) {
        validate(request);
        Product product = request.id() == null
                ? new Product()
                : products.findById(request.id()).orElseThrow(() -> new NoSuchElementException("商品不存在"));
        product.setName(request.name().trim());
        product.setPrice(request.price());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setProductUrl(request.productUrl());
        product.setCategory(request.categoryId() == null
                ? null
                : categories.findById(request.categoryId())
                        .orElseThrow(() -> new IllegalArgumentException("種類編號不存在：" + request.categoryId())));
        return products.save(product);
    }

    private void validate(ProductRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("商品名稱不可空白");
        }
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("商品價格必須大於或等於 0");
        }
    }

    @Transactional
    public void delete(Long id) {
        products.deleteById(id);
    }

    public String dao() {
        return products.name();
    }

    public record ProductRequest(Long id, String name, BigDecimal price, String description,
            String imageUrl, String productUrl, Long categoryId) {
    }

    public record ProductDto(Long id, String name, BigDecimal price, String description,
            String imageUrl, String productUrl, Long categoryId, String categoryName) {
        static ProductDto of(Product product) {
            Category category = product.getCategory();
            return new ProductDto(product.getId(), product.getName(), product.getPrice(),
                    product.getDescription(), product.getImageUrl(), product.getProductUrl(),
                    category == null ? null : category.getId(),
                    category == null ? "未分類" : category.getName());
        }
    }

    public record ImportResult(int parsedCount, int savedCount, String dao, List<Long> createdIds,
            String transactionStatus) {
    }
}
