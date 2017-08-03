package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.record.service.RecordService;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
@MaggieAction(path = "kid/wechat/recordBuy")
public class RecordBuyAction implements IAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private RecordService recordService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {

        context.set("msg", "飞行日志礼物购买成功!");
        context.set("code", "0");
        if (null != context.parameter("serialNumber")) {
            String serialNumber = String.valueOf(context.parameter("serialNumber"));
            context.set("data", JSONObject.convertFromObject(recordService.buyRecords(serialNumber)));
        }else
        if (null != context.parameter("id")) {
            Integer id = Integer.valueOf(String.valueOf(context.parameter("id")));
            context.set("data", JSONObject.convertFromObject(recordService.buyRecord(id)));
        }else{
            context.set("msg", "飞行日志礼物购买失败,缺少参数!");
            context.set("code", "1");
        }
        return "success.json";
    }

}
