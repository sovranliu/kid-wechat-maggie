package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.CommonTool;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger(GiveTicketAction.class);


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
        logger.info("[kid/wechat/giveTicket]-in:" + serialNumber);
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
        String mobileNo = (String)context.parameter("phone");

        String result = ticketService.handselTickets(ticketEntity.id, mobileNo, ticketEntity.telephone, CommonTool.HANDLE_GIVE);
        if(!"success".equals(result)) {
            context.set("msg", result);
            context.set("code", -1);
        }
        return "success.json";
    }
}
