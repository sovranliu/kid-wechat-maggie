package com.xyzq.kid.wechat.book.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;

@MaggieAction(path="kid/wechat/timespan/init")
public class InitBookTimeSpanAction implements IAction{
	
	@Autowired
	BookTimeSpanService bookTimeSpanService;	
	
	@Override
	public String execute(Visitor visitor, Context context) throws Exception {
		bookTimeSpanService.initTimeSpan(9, 15, 30);
		context.set("code", 0);
		return "success.json";
	}

}
