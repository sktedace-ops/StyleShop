package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.CartMapper;
import com.example.vscodeshop.model.CartItem;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CartRepositoryImpl implements CartRepository {
    private final CartMapper mapper;
    public CartRepositoryImpl(CartMapper mapper) { this.mapper = mapper; }
    public List<CartItem> findByUsername(String username) { return mapper.findByUsername(username); }
    public CartItem findById(Long id) { return mapper.findById(id); }
    public CartItem findByUsernameAndProductId(String username, Long productId) {
        return mapper.findByUsernameAndProductId(username, productId);
    }
    public int insert(CartItem item) { return mapper.insert(item); }
    public int updateQuantity(Long id, Integer quantity) { return mapper.updateQuantity(id, quantity); }
    public int deleteByIdAndUsername(Long id, String username) { return mapper.deleteByIdAndUsername(id, username); }
    public int deleteByUsername(String username) { return mapper.deleteByUsername(username); }
}
