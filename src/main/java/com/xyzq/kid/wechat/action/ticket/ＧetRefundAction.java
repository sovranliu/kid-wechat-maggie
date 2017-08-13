package com.xyzq.kid.wechat.action.ticket;

import com.google.gson.Gson;
import com.xyzq.kid.logic.config.common.ConfigCommon;
import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.kid.logic.config.service.GoodsTypeService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.entity.TicketRefundEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增票
 */
@MaggieAction(path = "kid/wechat/getRefund")
public class ＧetRefundAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private TicketService ticketService;
    @Autowired
    private ConfigService configService;

    /**
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger(ＧetRefundAction.class);

    Gson gson=new Gson();
    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {

        String serialNumber = (String) context.parameter("serialNumber");
        logger.info("[kid/wechat/getRefund]-in:" + serialNumber);
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
//        TicketRefundEntity ticketRefundEntity = ticketService.loadRefundByTicketId(ticketEntity.id);
        Map<String,Object> map=new HashMap<>();
        if(null != ticketEntity) {
            if(ticketEntity.insurance == true) {

                Map<String, Integer> pricemap = configService.getPriceInfo();
                int fee = Integer.valueOf(pricemap.get(ConfigCommon.FEE_INSURANCE).toString());
                fee =  ticketEntity.price.intValue() - fee;

                map.put("price", fee);
                map.put("isInsurance", true);
            } else {
                int price = (int) (ticketEntity.price.intValue() * 0.7);
                map.put("price", price);
                map.put("isInsurance", false);
            }

            map.put("expire", ticketEntity.expire);
            map.put("serialNumber", ticketEntity.serialNumber);
        }
        logger.info("[kid/wechat/getRefund]-out:" + gson.toJson(map));

        context.set("data", gson.toJson(map));
        return "success.json";
    }
}
