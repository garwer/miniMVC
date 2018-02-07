package com.linjw.miniMVC.service.imp;

import com.linjw.miniMVC.annotation.Qualifier;
import com.linjw.miniMVC.annotation.Service;
import com.linjw.miniMVC.dao.UserDao;
import com.linjw.miniMVC.service.UserService;

@Service("userServiceImpl")
public class UserServiceImp implements UserService{

    @Qualifier("userDaoImp")
    private UserDao userDao;

    public void insert() {
        System.out.println("执行UserServiceImp 方法开始");
        userDao.insert();
        System.out.println("执行UserServiceImp 方法结束");
    }
}
