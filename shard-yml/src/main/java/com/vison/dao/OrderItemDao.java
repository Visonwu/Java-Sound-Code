package com.vison.dao;

import com.vison.entity.OrderItem;

import java.util.List;

/**
 * @Author: vison
 * @Description:
 */
public interface OrderItemDao {

    void addOne(OrderItem orderItem);

    List<OrderItem> getByOrderId(int id);

    List<OrderItem> getOrderItemByPrice(int price);


}
