package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.Category;
import java.util.List;

public interface CategoryDao {
    List<Category> findAll();
    Category findById(Long id);
    Category findByName(String name);
    int insert(Category category);
    int update(Category category);
    int deleteById(Long id);
}
