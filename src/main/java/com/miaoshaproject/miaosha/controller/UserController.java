package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    //调用service服务获取对应id对象返回给前端
    public UserModel getUser(@RequestParam(name = "id") Integer id){
        UserModel userModel = userService.getUserById(id);
        return userModel;
    }
}
