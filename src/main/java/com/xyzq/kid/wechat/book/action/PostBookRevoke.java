package com.xyzq.kid.wechat.book.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.book.dao.po.Book;
import com.xyzq.kid.logic.book.service.BookChangeRequestService;
import com.xyzq.kid.logic.book.service.BookService;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/postRevoke")
public class PostBookRevoke extends WechatUserAjaxAction {
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	BookChangeRequestService bookChangeRequestService;
	
	@Autowired
	TicketService ticketService;
	
	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		UserEntity user=userService.selectByMolieNo(mobileNo);
		String serialNumber=(String)context.parameter("serialNumber");
		TicketEntity ticket=ticketService.getTicketsInfoBySerialno(serialNumber);
		Book book=bookService.queryBookRecByTicketId(ticket.id);
		if(book!=null){
			//1：改期申请，2：撤销申请
			if(bookChangeRequestService.createRequest(book.getId(), "2", null, user.id, book.getBooktimeid(),"1")){
				context.set("code", "0");
			}
		}
		return "success.json";
	}
}
