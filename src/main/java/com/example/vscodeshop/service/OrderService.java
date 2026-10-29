package com.example.vscodeshop.service;

import com.example.vscodeshop.repository.CartRepository;
import com.example.vscodeshop.repository.OrderRepository;
import com.example.vscodeshop.model.CartItem;
import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public List<Order> getOrders(String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        for (Order order : orders) {
            List<OrderItem> items = orderRepository.findItemsByOrderId(order.getId());
            attachOrder(order, items);
        }
        return orders;
    }

    public Optional<Order> getOrder(Long id, String username) {
        Order order = orderRepository.findByIdAndUsername(id, username);
        if (order == null) return Optional.empty();
        List<OrderItem> items = orderRepository.findItemsByOrderId(order.getId());
        attachOrder(order, items);
        return Optional.of(order);
    }

    @Transactional
    public Order createOrderFromCart(String username) {
        List<CartItem> cartItems = cartRepository.findByUsername(username);
        if (cartItems.isEmpty()) return null;

        Order order = new Order();
        order.setUsername(username);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("訂單成立");

        int total = cartItems.stream()
                .mapToInt(c -> c.getProduct().getPrice() * c.getQuantity()).sum();
        order.setTotalAmount(total);
        orderRepository.insertOrder(order);

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(cartItem.getProduct().getId());
            item.setProductName(cartItem.getProduct().getName());
            item.setImageUrl(cartItem.getProduct().getImageUrl());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setSubtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
            orderRepository.insertOrderItem(item);
            order.getItems().add(item);
        }

        cartRepository.deleteByUsername(username);
        return order;
    }

    @Transactional
    public boolean cancelOrder(Long id, String username) {
        return orderRepository.cancelOrder(id, username) > 0;
    }

    private void attachOrder(Order order, List<OrderItem> items) {
        for (OrderItem item : items) item.setOrder(order);
        order.setItems(items);
    }
}
