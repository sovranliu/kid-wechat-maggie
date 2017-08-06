package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
        if(null != result) {
            context.set("data", JSONObject.convertFromTable(result));
        }
        return "success.json";
    }
}
