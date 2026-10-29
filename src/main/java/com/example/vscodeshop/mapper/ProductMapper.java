package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.Product;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductMapper {
    List<Product> findAll();
    Product findById(@Param("id") Long id);
    Product findByName(@Param("name") String name);
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    int insert(Product product);
    int insertWithoutCategory(Product product);
    int update(Product product);
    int updateWithoutCategory(Product product);
    int deleteById(@Param("id") Long id);
}
