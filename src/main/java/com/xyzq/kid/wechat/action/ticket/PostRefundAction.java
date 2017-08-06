package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.CommonTool;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.entity.TicketRefundEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
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
@MaggieAction(path = "kid/wechat/postRefund")
public class PostRefundAction extends WechatUserAjaxAction {
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

        String serialNumber = (String) context.parameter("serialNumber", -1);
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
        TicketRefundEntity ticketRefundEntity = new TicketRefundEntity();
        if(null != ticketEntity) {
            ticketRefundEntity.deleted = CommonTool.STATUS_NORMAL;
            ticketRefundEntity.status = TicketRefundEntity.REFUND_STATUS_NEW;
            ticketRefundEntity.ticketid = ticketEntity.id;
            ticketService.insertRefundSelective(ticketRefundEntity);
        }

        return "success.json";
    }
}
