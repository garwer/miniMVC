package com.linjw.miniMVC.dao.imp;

import com.linjw.miniMVC.annotation.Qualifier;
import com.linjw.miniMVC.annotation.Repository;
import com.linjw.miniMVC.dao.UserDao;

@Repository("userDaoImp")
public class UserDaoImp implements UserDao{

    public void insert() {
        System.out.println("执行UserDaoImp内方法");
    }
}
