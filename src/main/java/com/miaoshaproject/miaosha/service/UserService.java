package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.apache.tomcat.jni.User;

public interface UserService {
    //通过对象id获取对象
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BussinessException;
    /*
     * 用户登录服务，校验用户登录是否合法
     * telphone：是用户注册手机
     * password：是用户加密后的密码
     */
    UserModel validateLogin(String telphone, String encrptPassword) throws BussinessException;
}
