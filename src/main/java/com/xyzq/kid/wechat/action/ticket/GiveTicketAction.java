package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 增票
 */
@MaggieAction(path = "kid/wechat/giveTicket")
public class GiveTicketAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private TicketService ticketService;


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
        String mobileNo = (String)context.parameter("phone");

        String result = ticketService.handselTickets(ticketEntity.id, mobileNo, ticketEntity.telephone);
        if(!"success".equals(result)) {
            context.set("code", -1);
        }
        return "success.json";
    }
}
