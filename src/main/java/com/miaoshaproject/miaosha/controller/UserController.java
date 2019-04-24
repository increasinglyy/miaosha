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

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //接入HttpSession,httpServletRequest通过bean的方式注入
    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户获取otp短信接口
    @RequestMapping(value = "getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone){
        //需要按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);//此时随机数取值[0,99999)
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);


        //将otp验证码同对应用户的手机号关联，
        //使用HTTP session的方式绑定手机号与otpCode(redis非常适用）
        httpServletRequest.getSession().setAttribute(telphone, otpCode);


        //将otp验证码通过短信通道发送给用户，省略
        System.out.println("telphone = "+telphone+" & optCode = " + otpCode);
        return CommonReturnType.create(null);
    }


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
