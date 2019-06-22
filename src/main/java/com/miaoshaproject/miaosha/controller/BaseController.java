package com.miaoshaproject.miaosha.controller;

import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.error.EmBusinessError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";


    //定义exceptionhandler解决未被controller层吸收的exception
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public Object handlerException(HttpServletRequest request, Exception ex){
//        Map<String, Object> responseData = new HashMap<>();
//        if (ex instanceof BussinessException){
//            //把其他异常强转为bussinessException
//            BussinessException bussinessException = (BussinessException)ex;
//
//            responseData.put("errCode", bussinessException.getErrCode());
//            responseData.put("errMsg", bussinessException.getErrMsg());
//        }else {
//            responseData.put("errCode", EmBusinessError.UNKNOW_ERROR.getErrCode());
//            responseData.put("errMsg", EmBusinessError.UNKNOW_ERROR.getErrMsg());
//        }
//
////        CommonReturnType commonReturnType = new CommonReturnType();
////        commonReturnType.setStatus("fail");
////        commonReturnType.setData(responseData);
//        return CommonReturnType.create(responseData, "fail");
//    }
}
