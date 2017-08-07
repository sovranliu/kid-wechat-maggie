package com.xyzq.kid.wechat.book.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.xyzq.kid.logic.book.dao.po.Book;
import com.xyzq.kid.logic.book.dao.po.BookTimeRepository;
import com.xyzq.kid.logic.book.dao.po.BookTimeSpan;
import com.xyzq.kid.logic.book.service.BookRepositoryService;
import com.xyzq.kid.logic.book.service.BookService;
import com.xyzq.kid.logic.book.service.BookTimeSpanService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/getBooks")
public class GetBooksAction extends WechatUserAjaxAction{
	
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
	public String doExecute(Visitor visitor, Context context) throws Exception {
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		List<TicketEntity> ticketList=ticketService.getTicketsInfoByOwnerMobileNo(mobileNo);
		List<Map<String,Object>> mapList=new ArrayList<>();
		if(ticketList!=null&&ticketList.size()>0){
			for(int i=0;i<ticketList.size();i++){
				TicketEntity ticket=ticketList.get(i);
				Book book=bookService.queryBookRecByTicketId(Integer.valueOf(ticket.id));
				if(book!=null){
					Map<String,Object> bookMap=new HashMap<>();
					bookMap.put("id", book.getId());
					String bookStatus=book.getBookstatus();//1：已预约，2：改期申请中，3：改期通过，4：改期拒绝，5：核销完成，6：撤销申请中，7：撤销通过，8：拒绝撤销
					Integer status=0;//0：已预约 1：已过期 2：已核销 3：改期审核中 4：已撤销 5：撤销申请中
					if(checkExpire(book.getBookdate())){
						status=1;
					}else{
						if(bookStatus.equals("1")||bookStatus.equals("3")||bookStatus.equals("4")||bookStatus.equals("8")){
							status=0;
						}else if(bookStatus.equals("2")){
							status=3;
						}else if(bookStatus.equals("6")){
							status=5;
						}else if(bookStatus.equals("7")){
							status=4;
						}else if(bookStatus.equals("5")){
							status=2;
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
		}
		mapList.sort(c);
		context.set("code", 0);
		context.set("data", gson.toJson(mapList));
		return "success.json";
	}
	
	static Comparator<Map<String,Object>> c=new Comparator<Map<String,Object>>() {
		@Override
		public int compare(Map<String, Object> o1,
				Map<String, Object> o2) {
			if((Integer)o1.get("status")<(Integer)o2.get("status")){
				return -1;
			}else{
				return 1;
			}
		}
	};
	
	
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
		
		Calendar todayCalendar=Calendar.getInstance();
		todayCalendar.setTime(new Date());
		todayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		todayCalendar.set(Calendar.MINUTE, 59);
		todayCalendar.set(Calendar.SECOND, 59);
		
		if(bookCalendar.before(todayCalendar)){
			return true;
		}else{
			return false;
		}
	}
	
//	public static void main(String[] args) {
//		List<Map<String,Object>> list=new ArrayList<>();
//		Map<String,Object> map1=new HashMap<>();
//		map1.put("status", 2);
//		map1.put("other", "test2");
//		list.add(map1);
//		Map<String,Object> map2=new HashMap<>();
//		map2.put("status", 0);
//		map2.put("other", "test1");
//		list.add(map2);
//		Map<String,Object> map3=new HashMap<>();
//		map3.put("status", 0);
//		map3.put("other", "test3");
//		list.add(map3);
//		Map<String,Object> map4=new HashMap<>();
//		map4.put("status", 1);
//		map4.put("other", "test1");
//		list.add(map4);
//		Map<String,Object> map5=new HashMap<>();
//		map5.put("status", 5);
//		map5.put("other", "test3");
//		list.add(map5);
//		list.sort(c);
//		for(int i=0;i<list.size();i++){
//			Map<String,Object> map=list.get(i);
//			System.out.println("status:"+map.get("status")+"--other:"+map.get("other"));
//		}
//	}
}
