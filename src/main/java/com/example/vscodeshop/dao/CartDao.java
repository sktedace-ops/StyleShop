package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.CartItem;
import java.util.List;

public interface CartDao {
    List<CartItem> findByUsername(String username);
    CartItem findById(Long id);
    CartItem findByUsernameAndProductId(String username, Long productId);
    int insert(CartItem item);
    int updateQuantity(Long id, Integer quantity);
    int deleteByIdAndUsername(Long id, String username);
    int deleteByUsername(String username);
}
