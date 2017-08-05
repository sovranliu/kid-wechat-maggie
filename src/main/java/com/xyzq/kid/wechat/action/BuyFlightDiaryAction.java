package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.record.entity.RecordEntity;
import com.xyzq.kid.logic.record.service.RecordService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 飞行日记获取
 * 不再使用
 */
@MaggieAction(path = "kid/wechat/buyFlightDiary")
public class BuyFlightDiaryAction implements IAction {
	/**
	 * Action中只支持Autowired注解引入SpringBean
	 */
	@Autowired
	private RecordService recordService;
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
	public String execute(Visitor visitor, Context context) throws Exception {
		String serialNo = String.valueOf(context.parameter("serialnumber"));

		List<RecordEntity> canPurchaseList = recordService.findBy(serialNo, RecordEntity.UNPURCHASED);

		if (canPurchaseList == null || canPurchaseList.isEmpty()) {
			context.set("code", "2");
			context.set("msg", "飞行票对应的飞行日志文件不存在!");
			return "success.json";
		}
		recordService.buyRecords(serialNo);

		context.set("code", "0");
		context.set("msg", "成功");
		return "success.json";
	}


}
