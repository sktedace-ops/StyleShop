package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.ProductMapper;
import com.example.vscodeshop.model.Product;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductMapper mapper;
    public ProductRepositoryImpl(ProductMapper mapper) { this.mapper = mapper; }
    public List<Product> findAll() { return mapper.findAll(); }
    public Product findById(Long id) { return mapper.findById(id); }
    public Product findByName(String name) { return mapper.findByName(name); }
    public List<Product> findByCategoryId(Long categoryId) { return mapper.findByCategoryId(categoryId); }
    public int insert(Product product) { return mapper.insert(product); }
    public int insertWithoutCategory(Product product) { return mapper.insertWithoutCategory(product); }
    public int update(Product product) { return mapper.update(product); }
    public int updateWithoutCategory(Product product) { return mapper.updateWithoutCategory(product); }
    public int deleteById(Long id) { return mapper.deleteById(id); }
}
