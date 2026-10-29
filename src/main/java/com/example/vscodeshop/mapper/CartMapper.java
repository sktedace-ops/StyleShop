package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.CartItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CartMapper {
    List<CartItem> findByUsername(@Param("username") String username);
    CartItem findById(@Param("id") Long id);
    CartItem findByUsernameAndProductId(@Param("username") String username,
                                        @Param("productId") Long productId);
    int insert(CartItem item);
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int deleteByIdAndUsername(@Param("id") Long id, @Param("username") String username);
    int deleteByUsername(@Param("username") String username);
}
