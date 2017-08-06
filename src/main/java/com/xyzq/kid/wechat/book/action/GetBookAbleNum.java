package com.xyzq.kid.wechat.book.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import com.xyzq.kid.logic.book.dao.po.BookTimeRepository;
import com.xyzq.kid.logic.book.dao.po.BookTimeSpan;
import com.xyzq.kid.logic.book.service.BookRepositoryService;
import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/getBookableNum")
public class GetBookAbleNum extends WechatUserAjaxAction {
	
	@Autowired
	BookRepositoryService bookRepositoryService;
	
	@Autowired
	BookTimeSpanService bookTimeSpanService;
	
	Gson gson=new Gson();
	
	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		String year=(String)context.parameter("year");
		String month=(String)context.parameter("month");
		String day=(String)context.parameter("day");
		String start=(String)context.parameter("start");
		String end=(String)context.parameter("end");
		System.out.println("year:"+year+" --"+"month:"+month+" --"+" day"+day+" -- start:"+start+" --end:"+end);
		String bookAbleNum="0";
		if(!StringUtils.isNullOrEmpty(year)&&!StringUtils.isNullOrEmpty(month)&&!StringUtils.isNullOrEmpty(day)){
			String bookDate=year+"-"+month+"-"+day;
			String timeSpan=start+"-"+end;
			BookTimeSpan bs=bookTimeSpanService.queryByTimeSpan(timeSpan);
			if(bs!=null){
				BookTimeRepository repo=bookRepositoryService.queryRepositoryByDateAndTimeSpan(bookDate, bs.getId());
				bookAbleNum=String.valueOf(repo.getBookamount());
			}
			Map<String,String> map=new HashMap<>();
			map.put("count", bookAbleNum);
			context.set("code", 0);
			context.set("data", gson.toJson(map));
		}
		return "success.json";
	}
	
}
