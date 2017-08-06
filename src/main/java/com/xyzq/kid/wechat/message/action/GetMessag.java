package com.xyzq.kid.wechat.message.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.kid.logic.message.dao.po.Message;
import com.xyzq.kid.logic.message.service.MessageService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

@MaggieAction(path="kid/wechat/getMessage")
public class GetMessag extends WechatUserAjaxAction {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	Gson gson=new Gson();
	
	@Override
	public String doExecute(Visitor visitor, Context context) throws Exception {
		String mobileNo=(String)context.get(CONTEXT_KEY_MOBILENO);
		UserEntity user=userService.selectByMolieNo(mobileNo);
		Message msg=messageService.queryLatestAnswer(user.id);
		Map<String,String> map=new HashMap<>();
		if(msg!=null){
			map.put("content", msg.getAnswer());
		}
		context.set("0", "0");
		context.set("data", gson.toJson(map));
		return "success.json";
	}
	
}
