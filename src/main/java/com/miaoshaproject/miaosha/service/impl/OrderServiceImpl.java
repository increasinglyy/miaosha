package com.miaoshaproject.miaosha.service.impl;

import com.miaoshaproject.miaosha.dao.OrderDOMapper;
import com.miaoshaproject.miaosha.dao.SequenceDOMapper;
import com.miaoshaproject.miaosha.dataobject.OrderDO;
import com.miaoshaproject.miaosha.dataobject.SequenceDO;
import com.miaoshaproject.miaosha.error.BussinessException;
import com.miaoshaproject.miaosha.error.EmBusinessError;
import com.miaoshaproject.miaosha.service.ItemService;
import com.miaoshaproject.miaosha.service.OrderService;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.ItemModel;
import com.miaoshaproject.miaosha.service.model.OrderModel;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;


    @Override
    @Transactional
    //amount为购买数量
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BussinessException {
        //1.校验下单状态（用户是否合法，下单商品是否存在，购买数量是否正确）
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        if (amount <= 0 || amount >100){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }

        //校验活动信息
        if (promoId != null){
            // (1) 校验对应活动是否存在对应商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            } else if (itemModel.getPromoModel().getStatus().intValue() != 2){
                // (2)校验活动是否在进行中
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动还未开始");
            }
        }


        //2.落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result){
            throw new BussinessException(EmBusinessError.STOCK_NOT_ENOUGH);//库存不足
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setUserId(userId);
        orderModel.setAmount(amount);
        if (promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }

        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));//总金额


        orderModel.setId(generateOrderNo());//生成交易流水号，订单号

        OrderDO orderDO = convertOrderDOFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品的销量
        itemService.increaseSales(itemId, amount);

        //4.返回前端

        return orderModel;
    }

    //获取订单号
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo(){
        //1.订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //2.中间六位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        //按步长更新数据库
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        //凑足六位
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6-sequenceStr.length(); i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);


        //3.最后两位分库分表位 00~99, 暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        return orderDO;
    }
}
