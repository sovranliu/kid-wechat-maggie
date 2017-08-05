package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.finance.service.OrderService;
import com.xyzq.simpson.base.xml.XMLNode;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 微信支付回调
 */
@MaggieAction(path = "kid/wechat/tencent/payNotify")
public class PayNotifyAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(PayNotifyAction.class);
    /**
     * 支付订单服务
     */
    @Autowired
    private OrderService orderService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String notify = (String) context.parameter("");
        logger.info("PayNotifyAction :\n" + notify);
        if(orderService.notifyPayment(notify)) {
            XMLNode return_code = new XMLNode();
            return_code.setName("return_code");
            return_code.setValue("SUCCESS");
            XMLNode return_msg = new XMLNode();
            return_msg.setName("return_msg");
            return_msg.setValue("OK");
            XMLNode xml = new XMLNode();
            xml.setName("xml");
            xml.children().add(return_code);
            xml.children().add(return_msg);
            context.set("content", xml.toString());
            logger.info("PayNotifyAction process success");
            return "empty.xml";
        }
        else {
            XMLNode return_code = new XMLNode();
            return_code.setName("return_code");
            return_code.setValue("FAIL");
            XMLNode return_msg = new XMLNode();
            return_msg.setName("return_msg");
            return_msg.setValue("NO");
            XMLNode xml = new XMLNode();
            xml.setName("xml");
            xml.children().add(return_code);
            xml.children().add(return_msg);
            context.set("content", xml.toString());
            logger.error("PayNotifyAction process fail");
            return "empty.xml";
        }
    }
}
