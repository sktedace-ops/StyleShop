package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.Product;
import java.util.List;

public interface ProductDao {
    List<Product> findAll();
    Product findById(Long id);
    Product findByName(String name);
    List<Product> findByCategoryId(Long categoryId);
    int insert(Product product);
    int insertWithoutCategory(Product product);
    int update(Product product);
    int updateWithoutCategory(Product product);
    int deleteById(Long id);
}
