package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.Order;
import com.example.vscodeshop.model.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    List<Order> findByUsername(@Param("username") String username);
    Order findByIdAndUsername(@Param("id") Long id, @Param("username") String username);
    List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);
    int insertOrder(Order order);
    int insertOrderItem(OrderItem item);
    int cancelOrder(@Param("id") Long id, @Param("username") String username);
}
