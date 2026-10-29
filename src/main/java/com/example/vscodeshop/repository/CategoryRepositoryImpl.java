package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.CategoryMapper;
import com.example.vscodeshop.model.Category;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryMapper mapper;
    public CategoryRepositoryImpl(CategoryMapper mapper) { this.mapper = mapper; }
    public List<Category> findAll() { return mapper.findAll(); }
    public Category findById(Long id) { return mapper.findById(id); }
    public Category findByName(String name) { return mapper.findByName(name); }
    public int insert(Category category) { return mapper.insert(category); }
    public int update(Category category) { return mapper.update(category); }
    public int deleteById(Long id) { return mapper.deleteById(id); }
}
