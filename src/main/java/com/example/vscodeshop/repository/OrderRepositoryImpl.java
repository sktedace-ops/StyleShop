package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.OrderMapper;
import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.model.OrderItem;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderMapper mapper;
    public OrderRepositoryImpl(OrderMapper mapper) { this.mapper = mapper; }
    public List<Order> findByUsername(String username) { return mapper.findByUsername(username); }
    public Order findByIdAndUsername(Long id, String username) { return mapper.findByIdAndUsername(id, username); }
    public List<OrderItem> findItemsByOrderId(Long orderId) { return mapper.findItemsByOrderId(orderId); }
    public int insertOrder(Order order) { return mapper.insertOrder(order); }
    public int insertOrderItem(OrderItem item) { return mapper.insertOrderItem(item); }
    public int cancelOrder(Long id, String username) { return mapper.cancelOrder(id, username); }
}
