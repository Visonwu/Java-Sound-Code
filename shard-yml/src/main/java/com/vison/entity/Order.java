package com.vison.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: vison
 * @Description:
 */
@Data
public class Order {

  private long orderId;
  private long userId;
  private Date createTime;
  private long totalPrice;

}
