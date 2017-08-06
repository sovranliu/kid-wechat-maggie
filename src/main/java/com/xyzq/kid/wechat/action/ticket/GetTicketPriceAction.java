package com.xyzq.kid.wechat.action.ticket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.xyzq.kid.logic.config.common.ConfigCommon;
import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/getTicketPrice")
public class GetTicketPriceAction implements IAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private ConfigService configService;
    
    Gson gson=new Gson();

    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        Map result = configService.getPriceInfo();
        Map<String,Object> map=new HashMap<>();
        if(result!=null&&result.size()>0){
        	map.put("single", map.get(ConfigCommon.FEE_SINGLETICKET));
        	map.put("group", map.get(ConfigCommon.FEE_GROUPTICKET));
        	map.put("refundInsurance", map.get(ConfigCommon.FEE_INSURANCE));
        }
        if(null != map) {
            context.set("data", gson.toJson(map));
        }
        return "success.json";
    }
}
