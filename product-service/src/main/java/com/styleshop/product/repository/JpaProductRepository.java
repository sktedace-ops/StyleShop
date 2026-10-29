package com.styleshop.product.repository;
import com.styleshop.product.model.Product; import org.springframework.data.jpa.repository.*; public interface JpaProductRepository extends JpaRepository<Product,Long>,JpaSpecificationExecutor<Product>{}
