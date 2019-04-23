package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.service.model.UserModel;
import org.apache.tomcat.jni.User;

public interface UserService {
    //通过对象id获取对象
    UserModel getUserById(Integer id);
}
