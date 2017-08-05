package com.xyzq.kid.wechat.message.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.message.dao.po.Message;
import com.xyzq.kid.logic.message.service.MessageService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/getMessageReply")
public class GetMessagReply extends WechatUserAjaxAction {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;

	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		UserEntity user=userService.selectByMolieNo(mobileNo);
		List<Message> msgLit=messageService.queryAllMessageByUserId(user.id);
		return "success.json";
	}
	
}
