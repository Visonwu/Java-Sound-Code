package com.vison.service;

import com.vison.dao.OrderItemDao;
import com.vison.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: vison
 * @Description:
 */
@Service
public class OrderItemService {
    @Autowired
    private OrderItemDao orderItemDao;

    public long addOne(OrderItem item){
        this.orderItemDao.addOne(item);
        return item.getOrderItemId();
    }



}
