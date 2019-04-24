package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.controller.viewobject.UserVO;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.UserModel;
import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.error.EmBusinessError;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller("user")
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    //调用service服务获取对应id对象返回给前端
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BussinessException {
        //调用service服务获取对应id对象返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在，抛出异常
        if (userModel == null){
            throw new BussinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //抛出errCode10002,
//        userModel=null;
//        userModel.setEncrptPassword("1");

        //将核心领域模型用户对象转化为可供UI使用的viewobject，减少password等字段
        UserVO userVO = convertFromModel(userModel);

        //返回userVO ---> 返回通用对象：status & data（两个字段）
        return CommonReturnType.create(userVO);
    }

    //将UserModel转化为UserVO
    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }




}
