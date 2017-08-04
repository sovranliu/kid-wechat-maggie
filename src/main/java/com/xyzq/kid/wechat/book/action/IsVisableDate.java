package com.xyzq.kid.wechat.book.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysql.jdbc.StringUtils;
import com.xyzq.kid.common.action.CustomerAction;
import com.xyzq.kid.logic.dateUnviable.entity.DateUnviableEntity;
import com.xyzq.kid.logic.dateUnviable.service.DateUnviableService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/isVisableDate")
public class IsVisableDate extends CustomerAction {
	
	@Autowired
	DateUnviableService dateUnviableService;

	@Override
	public String execute(Visitor visitor, Context context) throws Exception {
		String result=super.execute(visitor, context);
		if(result!=null){
			return result;
		}
		String unviableDate=(String)context.parameter("unviableDate");
		if(!StringUtils.isNullOrEmpty(unviableDate)){
			DateUnviableEntity po=dateUnviableService.findBy(unviableDate);
			if(po!=null){
				context.set("code", "0");
			}
		}
		
		return "success.json";
	}
	
	
}
