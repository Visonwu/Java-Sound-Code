package com.vison.dao;

import com.vison.entity.User;

/**
 * @Author: vison
 * @Description:
 */
public interface UserDao  {

    void addOne(User user);

    User getOneById(long id);
}
