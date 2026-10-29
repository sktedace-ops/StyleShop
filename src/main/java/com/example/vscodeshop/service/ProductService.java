package com.example.vscodeshop.service;

import com.example.vscodeshop.repository.ProductRepository;
import com.example.vscodeshop.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productRepository.findById(id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product save(Product product) {
        boolean hasCategory = product.getCategory() != null && product.getCategory().getId() != null;
        if (product.getId() == null) {
            if (hasCategory) productRepository.insert(product);
            else productRepository.insertWithoutCategory(product);
        } else {
            if (hasCategory) productRepository.update(product);
            else productRepository.updateWithoutCategory(product);
        }
        return product;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
