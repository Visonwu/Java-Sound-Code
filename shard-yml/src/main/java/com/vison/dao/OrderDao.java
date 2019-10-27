package com.vison.dao;

import com.vison.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: vison
 * @Description:
 */
public interface OrderDao {

    long addOne(Order order);

    Order selectOne(@Param("order_id") long orderId, @Param("user_id") int userId);

    List<Order> getOrderByUserId(long id);

}
