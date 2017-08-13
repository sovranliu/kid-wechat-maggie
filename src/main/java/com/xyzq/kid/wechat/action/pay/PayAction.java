package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.common.wechat.utility.WechatConfig;
import com.xyzq.kid.finance.service.OrderService;
import com.xyzq.kid.finance.service.entity.NewOrderEntity;
import com.xyzq.kid.logic.config.service.GoodsTypeService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.base.json.JSONString;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付动作
 */
@MaggieAction(path = "kid/wechat/pay")
public class PayAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(PayAction.class);
    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;
    /**
     * 支付订单服务
     */
    @Autowired
    private OrderService orderService;
    /**
     * 商品类型服务
     */
    @Autowired
    private GoodsTypeService goodsTypeService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        int goodsType = (Integer) context.parameter("goodsType", 0);
        if(0 == goodsType) {
            logger.error("empty goods type");
            context.set("msg", "商品类型为空");
            return "fail.json";
        }
        int fee = goodsTypeService.calculateFee(goodsType);
        if(0 == fee) {
            logger.error("invalid goods type : " + goodsType);
            context.set("msg", "商品类型异常");
            return "fail.json";
        }
        String openId = (String) context.parameter("openId");
        if(Text.isBlank(openId)) {
            logger.error("invalid open id : " + openId);
            context.set("msg", "微信用户开放ID为空");
            return "fail.json";
        }
        String mobileNo = (String) context.parameter("mobileNo");
        if(Text.isBlank(mobileNo)) {
            logger.error("empty mobile number when pay");
            context.set("msg", "购买者手机号码不能为空");
            return "fail.json";
        }
        String ownerOpenId = openId;
        UserEntity userEntity = userService.selectByMolieNo(mobileNo);
        if(null != userEntity) {
            ownerOpenId = userEntity.openid;
        }
        String goodsTypeTitle = goodsTypeService.getGoodsTypeTitle(goodsType);
        String ip = visitor.ip();
        NewOrderEntity newOrderEntity = null;
        if(goodsTypeService.isRecord(goodsType)) {
            String serialNo = (String) context.parameter("serialNo");
            if(Text.isBlank(serialNo)) {
                context.set("msg", "请选择指定飞行票下的飞行日志进行购买");
                return "fail.json";
            }
            newOrderEntity = orderService.createOrder(null, openId, goodsTypeTitle, goodsType, fee, ip, serialNo);
        }
        else {
            newOrderEntity = orderService.createOrder(null, openId, goodsTypeTitle, goodsType, fee, ip, ownerOpenId);
        }
        if(null == newOrderEntity) {
            logger.error("pay inner error, openId = " + openId + ", goodsType = " + goodsType + ", fee = " + fee + ", ip = " + ip + ", mobileNo = " + mobileNo);
            context.set("msg", "支付内部错误");
            return "fail.json";
        }
        JSONObject data = new JSONObject();
        data.put("orderNo", new JSONString(newOrderEntity.orderNo));
        data.put("openId", new JSONString(newOrderEntity.openId));
        data.put("appId", new JSONString(WechatConfig.appId));
        data.put("prepayId", new JSONString(newOrderEntity.prepayId));
        data.put("nonceString", new JSONString(newOrderEntity.nonceString));
        data.put("signType", new JSONString(newOrderEntity.signType));
        data.put("timestamp", new JSONString("" + newOrderEntity.timestamp));
        data.put("signature", new JSONString("" + newOrderEntity.signature));
        //
        context.set("data", data);
        return "success.json";
    }
}
