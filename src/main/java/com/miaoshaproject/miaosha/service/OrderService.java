package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BussinessException;
}
