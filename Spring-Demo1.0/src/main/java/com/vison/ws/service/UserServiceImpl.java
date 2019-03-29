package com.vison.ws.service;

import com.vison.ws.annotation.MyService;

@MyService
public class UserServiceImpl implements UserService {
    @Override
    public String get(String name) {
        return "收到信息"+name;
    }
}
