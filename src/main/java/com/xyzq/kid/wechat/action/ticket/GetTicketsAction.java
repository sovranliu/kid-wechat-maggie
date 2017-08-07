package com.xyzq.kid.wechat.action.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

/**
 * 增票
 */
@MaggieAction(path = "kid/wechat/getTickets")
public class GetTicketsAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private TicketService ticketService;

    /**
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger(GetTicketsAction.class);

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
        String mobileNo = (String) context.get(CONTEXT_KEY_MOBILENO);

        logger.info("[kid/wechat/getTickets]-in:" + mobileNo);

        List<Map<String,Object>> mapList=new ArrayList<>();
        List<TicketEntity> ticketList=ticketService.getTicketsInfoByOwnerMobileNo(mobileNo);
        if(ticketList!=null&&ticketList.size()>0){
        	 for(TicketEntity ticket:ticketList){
             	Map<String,Object> map=new HashMap<>();
                 int count = ticketService.queryTickethandselCount(ticket.id);
                 if(count == 0 && ticket.type == TicketEntity.TICKET_TYPE_GROUP) {
                     map.put("isGive", true);
                 } else {
                     map.put("isGive", false);
                 }
             	map.put("id",ticket.id);
             	map.put("serialNumber", ticket.serialNumber);
             	map.put("type", ticket.type);
             	map.put("price", ticket.price);
             	map.put("purchaser", ticket.payeropenid);
             	map.put("owner", ticket.telephone);
             	map.put("expire", ticket.expire);
             	map.put("status", ticket.status);
             	mapList.add(map);
             }
        }
        context.set("data", gson.toJson(mapList));
        logger.info("[kid/wechat/getTickets]-out:" + gson.toJson(mapList));

        return "success.json";
    }
}
