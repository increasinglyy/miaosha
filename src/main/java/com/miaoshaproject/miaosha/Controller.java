package com.miaoshaproject.miaosha;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class Controller {
    @RequestMapping("test")
    public String index() {
        return "hello";
    }
}

