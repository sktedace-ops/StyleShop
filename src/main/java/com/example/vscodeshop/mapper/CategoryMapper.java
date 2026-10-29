package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CategoryMapper {
    List<Category> findAll();
    Category findById(@Param("id") Long id);
    Category findByName(@Param("name") String name);
    int insert(Category category);
    int update(Category category);
    int deleteById(@Param("id") Long id);
}
