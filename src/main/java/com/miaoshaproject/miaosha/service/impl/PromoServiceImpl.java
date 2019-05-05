package com.miaoshaproject.miaosha.service.impl;

import com.miaoshaproject.miaosha.dao.PromoDOMapper;
import com.miaoshaproject.miaosha.dataobject.PromoDO;
import com.miaoshaproject.miaosha.service.PromoService;
import com.miaoshaproject.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoServiceImpl implements PromoService {

    //引入数据库
    @Autowired
    private PromoDOMapper promoDOMapper;


    //根据itemId在promo数据库中 获取即将进行或者正在进行的秒杀活动
    @Override
    public PromoModel getPromoById(Integer itemId) {
        //获取对应商品的秒杀信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        //DO -> model
        PromoModel promoModel = convertFromPromoDO(promoDO);
        if (promoModel == null){
            return null;
        }

        //判断当前时间是否秒杀活动即将开始或正在进行。结束时间比现在还要前面，表示还未结束
        //（前端根据promoStatus来显示秒杀信息）
        if (promoModel.getStartDate().isAfterNow()){    //开始时间比现在还要后面，还未开始
            promoModel.setStatus(1);
        }else if (promoModel.getEndDate().isBeforeNow()){   //结束时间比现在前面，结束
            promoModel.setStatus(3);
        }else {
            promoModel.setStatus(2);    //正在进行
        }

        return promoModel;
    }

    //DO -> model
    private PromoModel convertFromPromoDO(PromoDO promoDO){
        if (promoDO== null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
