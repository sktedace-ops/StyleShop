package com.styleshop.product.repository;
import com.styleshop.product.model.Category; import org.springframework.data.jpa.repository.JpaRepository; public interface CategoryRepository extends JpaRepository<Category,Long>{java.util.Optional<Category> findByName(String name);}
