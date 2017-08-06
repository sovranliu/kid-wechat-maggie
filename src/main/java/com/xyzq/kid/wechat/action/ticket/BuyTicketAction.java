package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/buyTicket")
public class BuyTicketAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private TicketService ticketService;
    @Autowired
    private ConfigService configService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {

//        String result = super.execute(visitor, context);
//        if(null != result) {
//            return result;
//        }
//
//        TicketEntity ticketEntity = new TicketEntity();
//
//        ticketEntity.ownermobileno = (String) context.get(CONTEXT_KEY_MOBILENO);
//        ticketEntity.payeropenid = (String) context.get(CONTEXT_KEY_OPENID);
//        String insurance = (String)context.parameter("needRefundInsurance");
//        if("true".equals(insurance)) {
//            ticketEntity.insurance = true;
//        } else if("false".equals(insurance)) {
//            ticketEntity.insurance = false;
//        }
//        //TODO 获取orderno
//
//        int type = (Integer)context.parameter("ticketType", -1);
//        if(type == TicketEntity.TICKET_TYPE_SINGLE) {
//            int price = Integer.parseInt(configService.selectContentByName(ConfigCommon.PRICE_SINGLE));
//            ticketEntity.price = new BigDecimal(price);
//            ticketService.buySingleTickets(ticketEntity);
//        } else if(type == TicketEntity.TICKET_TYPE_GROUP) {
//            int price = Integer.parseInt(configService.selectContentByName(ConfigCommon.PRICE_GROUP));
//            ticketEntity.price = new BigDecimal(price);
//            int num = (Integer)context.parameter("ticketNum", -1);
//            ticketService.buyGroupleTickets(ticketEntity, num);
//        }

        return "success.json";
    }
}
