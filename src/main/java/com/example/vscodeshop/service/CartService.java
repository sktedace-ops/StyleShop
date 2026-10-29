package com.example.vscodeshop.service;

import com.example.vscodeshop.repository.CartRepository;
import com.example.vscodeshop.repository.ProductRepository;
import com.example.vscodeshop.model.CartItem;
import com.example.vscodeshop.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(String username) {
        return cartRepository.findByUsername(username);
    }

    public void addProduct(String username, Long productId) {
        Product product = productRepository.findById(productId);
        if (product == null) return;

        CartItem existing = cartRepository.findByUsernameAndProductId(username, productId);
        if (existing != null) {
            int quantity = existing.getQuantity() == null ? 1 : existing.getQuantity();
            cartRepository.updateQuantity(existing.getId(), quantity + 1);
        } else {
            cartRepository.insert(new CartItem(username, product, 1));
        }
    }

    public void increase(Long cartItemId, String username) {
        CartItem item = cartRepository.findById(cartItemId);
        if (item != null && username.equals(item.getUsername())) {
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            cartRepository.updateQuantity(item.getId(), quantity + 1);
        }
    }

    public void decrease(Long cartItemId, String username) {
        CartItem item = cartRepository.findById(cartItemId);
        if (item != null && username.equals(item.getUsername())) {
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            if (quantity <= 1) cartRepository.deleteByIdAndUsername(item.getId(), username);
            else cartRepository.updateQuantity(item.getId(), quantity - 1);
        }
    }

    public void remove(Long cartItemId, String username) {
        cartRepository.deleteByIdAndUsername(cartItemId, username);
    }

    public int getTotal(String username) {
        return getCartItems(username).stream()
                .filter(item -> item.getProduct() != null)
                .mapToInt(item -> {
                    int price = item.getProduct().getPrice() == null ? 0 : item.getProduct().getPrice();
                    int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
                    return price * quantity;
                }).sum();
    }

    public int getTotalQuantity(String username) {
        return getCartItems(username).stream()
                .mapToInt(item -> item.getQuantity() == null ? 1 : item.getQuantity())
                .sum();
    }

    public void clear(String username) {
        cartRepository.deleteByUsername(username);
    }

}
