package com.styleshop.product.repository;
import com.styleshop.product.model.Product; import org.springframework.data.domain.*; import java.math.BigDecimal; import java.util.Optional;
public interface ProductDataSource {Page<Product> search(String keyword,Long categoryId,BigDecimal minPrice,BigDecimal maxPrice,Pageable pageable);Optional<Product> findById(Long id);Product save(Product p);void deleteById(Long id);String name();}
