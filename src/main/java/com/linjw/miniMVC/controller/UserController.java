package com.linjw.miniMVC.controller;

import com.linjw.miniMVC.annotation.Controller;
import com.linjw.miniMVC.annotation.Qualifier;
import com.linjw.miniMVC.annotation.RequestMapping;
import com.linjw.miniMVC.service.UserService;

@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/insert")
    public void insert() {
        System.out.println("开始Controller方法");
        userService.insert();
    }
}
