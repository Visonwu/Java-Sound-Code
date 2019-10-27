package com.vison.entity;


import lombok.Data;

/**
 * @Author: vison
 * @Description:
 */
@Data
public class User {

    private long userId;
    private String name;
    private int age;

    public User() {
    }

    public User(long userId, String name, int age) {
        this.userId = userId;
        this.name = name;
        this.age = age;
    }
}
