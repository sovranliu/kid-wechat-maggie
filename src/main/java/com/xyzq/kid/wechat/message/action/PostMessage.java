package com.xyzq.kid.wechat.message.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysql.jdbc.StringUtils;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.message.service.MessageService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/postMessage")
public class PostMessage extends WechatUserAjaxAction {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;

	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		context.set("code", -9);
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		UserEntity user=userService.selectByMolieNo(mobileNo);
		String content=(String)context.parameter("content");
		if(user!=null&&!StringUtils.isNullOrEmpty(content)){
			if(messageService.createMessage(user.id, content)){
				context.set("code", 0);
			}
		}
		return "success.json";
	}
}
