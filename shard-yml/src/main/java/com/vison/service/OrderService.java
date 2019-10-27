package com.vison.service;

import com.vison.dao.OrderDao;
import com.vison.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: vison
 * @Description:
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public long insertOne(Order order) {
        this.orderDao.addOne(order);
        return order.getOrderId();
    }
}
