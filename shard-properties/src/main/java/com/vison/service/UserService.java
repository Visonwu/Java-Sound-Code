package com.vison.service;

import com.vison.entity.UserInfo;
import com.vison.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    UserInfoMapper userInfoMapper;

    public static Long userId = 1L;

    public void insert() {
        for (int i = 1; i <= 100; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setAccount("account" + i);
            userInfo.setPassword("password" + i);
            userInfo.setUserName("name" + i);
            userId++;
            userInfoMapper.insert(userInfo);
        }
    }

    public UserInfo getUserInfoByUserId(Long id){
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public List<UserInfo> selectByRange(Long firstId, Long lastId){
        return userInfoMapper.selectByRange(firstId, lastId);
    }
}
