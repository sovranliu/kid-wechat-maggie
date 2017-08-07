package com.xyzq.kid.wechat.book.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysql.jdbc.StringUtils;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.book.dao.po.Book;
import com.xyzq.kid.logic.book.dao.po.BookTimeRepository;
import com.xyzq.kid.logic.book.dao.po.BookTimeSpan;
import com.xyzq.kid.logic.book.service.BookChangeRequestService;
import com.xyzq.kid.logic.book.service.BookRepositoryService;
import com.xyzq.kid.logic.book.service.BookService;
import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/submitBooking")
public class SubmitBooking extends WechatUserAjaxAction {
	
	@Autowired
	BookRepositoryService bookRepositoryService;
	
	@Autowired
	TicketService ticketService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookTimeSpanService bookTimeSpanService;
	
	@Autowired
	BookChangeRequestService bookChangeRequestService;
	
	@Autowired
	BookService bookService;
	
	static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		context.set("code", -9);
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		UserEntity user=userService.selectByMolieNo(mobileNo);
		String serialNumber=(String)context.parameter("serialNumber");
		String year=(String)context.parameter("year");
		String month=(String)context.parameter("month");
		String day=(String)context.parameter("day");
		String start=(String)context.parameter("start");
		String end=(String)context.parameter("end");
		String type=(String)context.parameter("type");//0：预约提交，1：预约改期
		System.out.println("SerialNumber:"+serialNumber+" --"+"year:"+year+" --"+"month:"+month+" --"+" day"+day+" -- start:"+start+" --end:"+end+" --type:"+type);
		TicketEntity ticket=ticketService.getTicketsInfoBySerialno(serialNumber);
		if(!StringUtils.isNullOrEmpty(year)&&!StringUtils.isNullOrEmpty(month)&&!StringUtils.isNullOrEmpty(day)){
			String bookDate=year+"-"+month+"-"+day;
			String timeSpan=start+"-"+end;
			BookTimeSpan bs=bookTimeSpanService.queryByTimeSpan(timeSpan);
			if(checkDate(bookDate,start)&&bs!=null){
				BookTimeRepository repo=bookRepositoryService.queryRepositoryByDateAndTimeSpan(bookDate, bs.getId());
				if(!StringUtils.isNullOrEmpty(type)){
					if(type.equals("0")){//预约提交
						if(bookService.createBook(serialNumber, repo.getId(), user.id)){
							ticketService.useTickets(ticket.id);//票状态更新为已使用
							context.set("code", 0);
						}
					}else if(type.equals("1")){//改期提交
						Book book=bookService.queryBookRecByTicketId(ticket.id);
						if(book!=null){
							if(bookChangeRequestService.createRequest(book.getId(), "1", null, user.id, repo.getId(),"1")){
								context.set("code", 0);
							}
						}
					}
				}
			}
		}
		return "success.json";
	}
	
	static boolean  checkDate(String bookDate,String startTime) throws ParseException{
		boolean flag=false;
		Date bookTime=sdf.parse(bookDate + " "+startTime);
		Calendar bookCal=Calendar.getInstance();
		bookCal.setTime(bookTime);
		if(System.currentTimeMillis()-bookCal.getTimeInMillis()<0){
			flag=true;
		}
		return flag;
	}

}
