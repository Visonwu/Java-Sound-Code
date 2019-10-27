package com.vison;

import com.vison.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: vison
 * @Date: 2019/10/21 11:24
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TransactionTest {

    @Autowired
    private UserService userService;

    @Test
    public void transTest() {

        this.userService.testTransactional();
    }
}
