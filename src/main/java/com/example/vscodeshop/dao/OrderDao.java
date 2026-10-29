package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.model.OrderItem;
import java.util.List;

public interface OrderDao {
    List<Order> findByUsername(String username);
    Order findByIdAndUsername(Long id, String username);
    List<OrderItem> findItemsByOrderId(Long orderId);
    int insertOrder(Order order);
    int insertOrderItem(OrderItem item);
    int cancelOrder(Long id, String username);
}
