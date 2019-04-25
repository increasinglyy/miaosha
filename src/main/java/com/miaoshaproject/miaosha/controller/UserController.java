package com.miaoshaproject.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.miaosha.controller.viewobject.UserVO;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.UserModel;
import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.error.EmBusinessError;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


    //用户登录接口
    @RequestMapping(value = "login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验（手机号密码不能为空）
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)
                || org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "不能为空");
        }

        //用户登录服务，校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMD5(password));

        //将登陆凭证加入到用户登录成功的session中
        //如果用户的会话标识中有IS_LOGIN，则表示登录成功
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);//key:IS_LOGIN
        //将userModel放到对应的session里
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        //返回前端一个正确的信息
        return CommonReturnType.create(null);//success
    }

    //用户注册接口
    @RequestMapping(value = "register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpcode相符合（otpcode放在httpServletRequest）
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if (!StringUtils.equals(otpCode, inSessionOtpCode)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMD5(password));//将密码加密存入数据库

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncodeByMD5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newstr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }


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
