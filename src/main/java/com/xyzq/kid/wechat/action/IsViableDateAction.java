package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.dateUnviable.service.DateUnviableService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/isViableDate")
public class IsViableDateAction implements IAction {
	/**
	 * Action中只支持Autowired注解引入SpringBean
	 */
	@Autowired
	private DateUnviableService dateUnviableService;


	/**
	 * 动作执行
	 *
	 * @param visitor 访问者
	 * @param context 请求上下文
	 * @return 下一步动作，包括后缀名，null表示结束
	 */
	@Override
	public String execute(Visitor visitor, Context context) throws Exception {
		if (context.parameter("unviableDate") == null) {
			context.set("code", "1");
			context.set("msg", "参数不能为空！");
			return "success.json";
		}
		if (dateUnviableService.findBy(String.valueOf(context.parameter("unviableDate"))) == null) {
			context.set("code", "0");
			context.set("msg", "该日期是可预约日期！");
		} else {
			context.set("code", "1");
			context.set("msg", "该日期是不可预约日期！");
		}
		return "success.json";
	}
}
