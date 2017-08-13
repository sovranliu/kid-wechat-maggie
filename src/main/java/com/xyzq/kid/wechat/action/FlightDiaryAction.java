package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.config.common.ConfigCommon;
import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.kid.logic.record.entity.RecordEntity;
import com.xyzq.kid.logic.record.service.RecordService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.base.json.JSONArray;
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
public class FlightDiaryAction extends WechatUserAjaxAction {
	/**
	 * Action中只支持Autowired注解引入SpringBean
	 */
	@Autowired
	private RecordService recordService;
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

		String mobileNo = String.valueOf(context.get(CONTEXT_KEY_MOBILENO));
		//查询已使用票
		List<TicketEntity> ticketEntityList = ticketService.getTicketsInfoByOwnerMobileNo(mobileNo);
		if (ticketEntityList == null || ticketEntityList.isEmpty()) {
			context.set("code", "0");
			context.set("data", JSONArray.convert(null));
			return "success.json";
		}
		List<String> usedTIcketSerialNoList = new ArrayList<>();
		for (TicketEntity entity : ticketEntityList) {
			//搜集已使用票的流水号
			if (TicketEntity.TICKET_STATUS_USED == entity.status) {
				usedTIcketSerialNoList.add(entity.serialNumber);
			}
		}
		List<Map<String, Object>> canPurchaseMapList = new ArrayList<Map<String, Object>>();
		if (usedTIcketSerialNoList != null) {
			//如果存在使用过的票，则分别查询每个票下面的未购买视频列表。
			for (String serialNo : usedTIcketSerialNoList) {
				List<String> serialNoList = new ArrayList<>();
				serialNoList.add(serialNo);
				List<RecordEntity> unPurchaseList = recordService.findBy(serialNoList, RecordEntity.UNPURCHASED);
				//如果不存在视频，则不返回
				if (unPurchaseList != null && unPurchaseList.size() > 0) {
					Map<String, Object> map = new HashMap<>();
					map.put("serialNo", serialNo);
					map.put("records", transToMap(unPurchaseList, context));
					canPurchaseMapList.add(map);
				}

			}
		}
//		List<RecordEntity> unPurchaseList = recordService.findBy(usedTIcketSerialNoList, RecordEntity.UNPURCHASED);
		List<RecordEntity> hasPurchasedList = recordService.findBy(usedTIcketSerialNoList, RecordEntity.PURCHASED);
		Integer price = Integer.valueOf(configService.fetch(ConfigCommon.FEE_RECORD));
		Integer accumulateTime = Integer.valueOf(configService.fetch(ConfigCommon.TICKET_ACCUMULATE_TIME));
		//获取所有已经购买飞行日志的票券流水号，放入map
		Map<String, String> hasPurchasedTicketSerialNoMap = new HashMap<String, String>();
		for (RecordEntity record : hasPurchasedList) {
			hasPurchasedTicketSerialNoMap.put(record.serialNo, record.serialNo);
		}


		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("canPurchase", canPurchaseMapList);
		resultMap.put("hasPurchased", transToMap(hasPurchasedList, context));
		resultMap.put("timeDuration", usedTIcketSerialNoList == null ? 0 : usedTIcketSerialNoList.size() * accumulateTime);
		resultMap.put("canPurchasePrice", usedTIcketSerialNoList == null ? 0 : (usedTIcketSerialNoList.size() - hasPurchasedTicketSerialNoMap.size()) * price);
		context.set("code", "0");
		context.set("data", JSONObject.convertFromTable(resultMap));
		return "success.json";
	}

	private List<Map<String, Object>> transToMap(List<RecordEntity> entities, Context context) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		if (entities != null && entities.size() > 0) {
			for (RecordEntity entity : entities) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", String.valueOf(entity.id));
				map.put("url", context.rootUrl() + entity.path);
				maps.add(map);
			}
		}
		return maps;
	}


}
