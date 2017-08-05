package com.xyzq.kid.wechat.book.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.book.dao.po.BookTimeRepository;
import com.xyzq.kid.logic.book.dao.po.BookTimeSpan;
import com.xyzq.kid.logic.book.service.BookRepositoryService;
import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/getBookingTime")
public class GetBookingTime extends WechatUserAjaxAction {
	
	@Autowired
	BookRepositoryService bookRepositoryService;
	@Autowired
	BookTimeSpanService bookTimeSpanService;
	
	Gson gson=new Gson();
	
	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		List<Map<String,String>> spanList=new ArrayList<>();
		String year=(String)context.parameter("year");
		String month=(String)context.parameter("month");
		String day=(String)context.parameter("day");
		if(!StringUtils.isNullOrEmpty(year)&&!StringUtils.isNullOrEmpty(month)&&!StringUtils.isNullOrEmpty(day)){
			String bookDate=year+"-"+month+"-"+day;
			List<BookTimeRepository> repos=bookRepositoryService.queryRepositoryByDate(bookDate);
			if(repos!=null&&repos.size()>0){
				for(BookTimeRepository repo:repos){
					Integer spanTimeId=repo.getBooktimespanid();
					BookTimeSpan bs=bookTimeSpanService.queryByPrimaryKey(spanTimeId);
					if(bs.getTimespan()!=null&&bs.getTimespan().contains("-")){
						Map<String,String> map=new HashMap<>();
						String[] times=bs.getTimespan().split("-");
						map.put("start", times[0]);
						map.put("end", times[1]);
						spanList.add(map);
					}
				}
			}
		}
		if(spanList.size()>0){
			context.set("code", 0);
			context.set("data", gson.toJson(spanList));
		}
		return "success.json";
	}

}
