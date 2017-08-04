package com.xyzq.kid.wechat.book.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.xyzq.kid.common.action.CustomerAction;
import com.xyzq.kid.logic.book.dao.po.Book;
import com.xyzq.kid.logic.book.dao.po.BookTimeRepository;
import com.xyzq.kid.logic.book.dao.po.BookTimeSpan;
import com.xyzq.kid.logic.book.service.BookRepositoryService;
import com.xyzq.kid.logic.book.service.BookService;
import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/book/getBooks")
public class GetBooksAction extends CustomerAction{
	
	@Autowired
	BookService bookService;
	
	@Autowired
	TicketService ticketService;
	
	@Autowired
	BookRepositoryService bookRepositoryService;
	
	@Autowired
	BookTimeSpanService bookTimeSpanService;
	
	Gson gson=new Gson();
	
	@Override
	public String execute(Visitor visitor, Context context) throws Exception {
		String result=super.execute(visitor, context);
		if(result!=null){
			return result;
		}
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		List<TicketEntity> ticketList=ticketService.getTicketsInfoByOwnerMobileNo(mobileNo);
		List<Map<String,String>> mapList=new ArrayList<>();
		if(ticketList!=null&&ticketList.size()>0){
			for(int i=0;i<=ticketList.size();i++){
				TicketEntity ticket=ticketList.get(i);
				Book book=bookService.queryBookRecByTicketId(Integer.valueOf(ticket.serialNumber));
				if(book!=null){
					Map<String,String> bookMap=new HashMap<>();
					bookMap.put("id", String.valueOf(i));
					String bookStatus=book.getBookstatus();//1：已预约，2：改期申请中，3：改期通过，4：改期拒绝，5：核销完成，6：撤销申请中，7：撤销通过，8：拒绝撤销
					String status="0";//0：已预约 1：已过期 2：已核销 3：改期审核中 4：已撤销 5：撤销申请中
					if(checkExpire(book.getBookdate())){
						status="1";
					}else{
						if(bookStatus.equals("1")||bookStatus.equals("3")||bookStatus.equals("4")||bookStatus.equals("8")){
							status="0";
						}else if(bookStatus.equals("2")){
							status="3";
						}else if(bookStatus.equals("6")){
							status="5";
						}else if(bookStatus.equals("7")){
							status="4";
						}else if(bookStatus.equals("5")){
							status="2";
						}
					}
					BookTimeRepository repo=bookRepositoryService.queryByPrimaryKey(book.getBooktimeid());
					if(repo!=null){
						BookTimeSpan span=bookTimeSpanService.queryByPrimaryKey(repo.getBooktimespanid());
						if(span!=null){
							String time=span.getTimespan();
							if(time.contains("-")){
								String[] times=time.split("-");
								bookMap.put("start", times[0]);
								bookMap.put("end", times[1]);
							}
						}
					}
					bookMap.put("status", status);
					bookMap.put("expire", book.getBookdate());
					bookMap.put("serialNumber", String.valueOf(ticket.serialNumber));
					mapList.add(bookMap);
				}
			}
			if(mapList.size()>0){
				context.set("code", "0");
				context.set("data", gson.toJson(mapList));
			}
		}
		return "success.json";
	}
	/**
	 * 验证预约时间是否已过期
	 * @param bookDate
	 * @return
	 */
	private static boolean checkExpire(String bookDate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar bookCalendar=Calendar.getInstance();
		String[] dates=bookDate.split("-");
		try {
			Date d=sdf.parse(dates[0]+"-"+dates[1]+"-"+dates[2]+" 00:00:00");
			bookCalendar.setTime(d);;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
//		System.out.println(sdf.format(bookCalendar.getTime()));
		
		Calendar todayCalendar=Calendar.getInstance();
		todayCalendar.setTime(new Date());
		todayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		todayCalendar.set(Calendar.MINUTE, 59);
		todayCalendar.set(Calendar.SECOND, 59);
		
//		System.out.println(sdf.format(todayCalendar.getTime()));
		if(bookCalendar.before(todayCalendar)){
			return true;
		}else{
			return false;
		}
	}
	public static void main(String[] args) {
//		Gson gson=new Gson();
//		List<Map<String,String>> mapList=new ArrayList<>();
//		Map<String,String> bookMap=new HashMap<>();
//		for(int i=0;i<5;i++){
//			bookMap.put("status", String.valueOf(i));
//			bookMap.put("expire", "2017-08-20");
//			bookMap.put("serialNumber", String.valueOf(i+1));
//			mapList.add(bookMap);
//		}
//		System.out.println(gson.toJson(mapList));
		if(!checkExpire("2017-3-20")){
			System.out.println("过期");
		}else{
			System.out.println("有效");
		}
	}
}
