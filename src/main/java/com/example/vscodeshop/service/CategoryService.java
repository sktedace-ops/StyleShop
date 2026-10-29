package com.example.vscodeshop.service;

import com.example.vscodeshop.repository.CategoryRepository;
import com.example.vscodeshop.model.Category;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return Optional.ofNullable(categoryRepository.findById(id));
    }

    public Category save(Category category) {
        if (category.getId() == null) categoryRepository.insert(category);
        else categoryRepository.update(category);
        return category;
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
