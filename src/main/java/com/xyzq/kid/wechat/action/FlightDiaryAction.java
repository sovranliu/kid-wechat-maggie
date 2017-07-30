package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.record.entity.RecordEntity;
import com.xyzq.kid.logic.record.service.RecordService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 飞行日记获取
 */
@MaggieAction(path = "kid/wechat/getFlightDiary")
public class FlightDiaryAction implements IAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private RecordService recordService;
    @Autowired
    private TicketService ticketService;
/*
    ## serialNumber  票号流水
    ## timeDuration  飞行总时长
    ## canPurchase  当前可购买的飞行日志，包含［总价格］和［各视频］
            ## hasPurchased  已购买的飞行日志

    {
        "code": 0,
        "data": {
            "serialNumber": 22030049,
            "timeDuration": 15,
            "canPurchase": {
                "price": "180",
                "videos": [
                {
                    "id": 1,
                        "url":"http://www.w3school.com.cn/i/movie.mp4"
                },
                {
                    "id": 2,
                        "url":"http://www.w3school.com.cn/i/movie.mp4"
                },
                {
                    "id": 3,
                        "url":"http://www.w3school.com.cn/i/movie.mp4"
                }
            ]
            },
            "hasPurchased": [
                {
                    "id": 4,
                        "url": "http://www.w3school.com.cn/i/movie.mp4"
                },
                {
                    "id": 5,
                        "url": "http://www.w3school.com.cn/i/movie.mp4"
                }
            ]
        }
    }
    */

    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String serialNumber = String.valueOf(context.parameter("serialNumber"));
        TicketEntity ticketEntity = ticketService.getTicketsInfoBySerialno(serialNumber);
        if (ticketEntity == null) {
            context.set("code", "1");
            context.set("msg", "飞行票serialNumber不存在!");
            return "success.json";
        }
        List<RecordEntity> canPurchaseList = recordService.findBy(ticketEntity.id, RecordEntity.UNPURCHASED);
        List<RecordEntity> hasPurchasedList = recordService.findBy(ticketEntity.id, RecordEntity.PURCHASED);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("serialNumber", serialNumber);
        resultMap.put("canPurchase", transToMap(canPurchaseList, context));
        resultMap.put("hasPurchased", transToMap(hasPurchasedList, context));
        resultMap.put("timeDuration", 13);
        resultMap.put("canPurchasePrice", canPurchaseList == null ? 0 : canPurchaseList.size() * 60);
        context.set("code", "0");
        context.set("msg", "查询成功");
        context.set("data", JSONObject.convertFromTable(resultMap));
        return "success.json";
    }

    private List<Map<String, Object>> transToMap(List<RecordEntity> entities, Context context) {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (RecordEntity entity : entities) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", String.valueOf(entity.id));
            map.put("url", context.rootUrl() + entity.path);
            maps.add(map);
        }
        return maps;
    }
}
