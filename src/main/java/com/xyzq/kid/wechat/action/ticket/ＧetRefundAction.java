package com.xyzq.kid.wechat.action.ticket;

import com.google.gson.Gson;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.entity.TicketRefundEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
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
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
        TicketRefundEntity ticketRefundEntity =ticketService.loadRefundByTicketId(ticketEntity.id);
        Map<String,Object> map=new HashMap<>();
        if(null != ticketRefundEntity) {
            map.put("expire", ticketEntity.expire);
            map.put("price", ticketEntity.price);
            map.put("serialNumber", ticketEntity.serialNumber);
        }

        context.set("data", gson.toJson(map));
        return "success.json";
    }
}
