package com.miaoshaproject.miaosha.service;

import com.miaoshaproject.miaosha.service.model.PromoModel;

public interface PromoService {
    //根据itemId获取即将进行或者正在进行的秒杀活动
    PromoModel getPromoById(Integer itemId);
}
