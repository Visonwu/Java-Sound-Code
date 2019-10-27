package com.vison.entity;


import lombok.Data;

/**
 * @Author: vison
 * @Description:
 */
@Data
public class OrderItem {

    private long userId;
    private long orderItemId;
    private long orderId;
    private String name;
    private long price;

}
