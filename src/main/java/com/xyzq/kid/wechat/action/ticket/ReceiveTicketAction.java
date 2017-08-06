package com.xyzq.kid.wechat.action.ticket;

import com.google.gson.Gson;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 增票
 */
@MaggieAction(path = "kid/wechat/receiveTicket")
public class ReceiveTicketAction extends WechatUserAjaxAction {
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
        String mobileNo = (String) context.parameter("mobileNo");

        Map<String,Object> map=new HashMap<>();
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
        if(null == ticketEntity || null == ticketEntity.telephone) {
            map.put("result", false);
            context.set("data", gson.toJson(map));
            return "success.json";
        }
        UserEntity userEntityOld = userService.selectByMolieNo(ticketEntity.telephone);
        UserEntity userEntity = userService.selectByMolieNo(mobileNo);
        if(null == userEntity || null == userEntity.userName) {
            map.put("result", false);
            context.set("data", gson.toJson(map));
            return "success.json";
        }

        map.put("serialNo", serialNumber);
        map.put("payerMobileNo", ticketEntity.telephone);
        map.put("payerName", userEntityOld.userName);

        if(!"success".equals(ticketService.handselTickets(ticketEntity.id, mobileNo, ticketEntity.telephone))) {
            map.put("result", false);
        } else {
            map.put("result", true);
        }

        context.set("data", gson.toJson(map));
        return "success.json";
    }
}
