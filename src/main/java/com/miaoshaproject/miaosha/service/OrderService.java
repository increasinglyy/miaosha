package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.service.model.OrderModel;

public interface OrderService {
    //1.通过前端url上传过来秒杀活动id，然后下单接口内校验对应id是否属于对应商品且活动已经开始
    //2.直接在下单接口内判断对应商品是否在秒杀活动，若存在进行中的 则以秒杀价格下单

    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BussinessException;
}
