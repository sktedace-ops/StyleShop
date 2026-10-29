package com.styleshop.product;
import org.mybatis.spring.annotation.MapperScan; import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication @MapperScan("com.styleshop.product.mapper") public class ProductServiceApplication {public static void main(String[]a){SpringApplication.run(ProductServiceApplication.class,a);}}
