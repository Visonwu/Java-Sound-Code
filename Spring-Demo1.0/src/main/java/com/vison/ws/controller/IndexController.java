package com.vison.ws.controller;


import com.vison.ws.annotation.MyAutowired;
import com.vison.ws.annotation.MyController;
import com.vison.ws.annotation.MyRequestMapping;
import com.vison.ws.annotation.MyRequestParam;
import com.vison.ws.service.UserService;

import javax.xml.ws.RequestWrapper;

@MyRequestMapping(value = "/ws")
@MyController
public class IndexController {

    @MyAutowired
    private UserService userService;

    @MyRequestMapping(value = "/get")
    public String get(@MyRequestParam("name") String name){
        return userService.get(name);
    }
}
