package com.xyzq.kid.wechat.action.member;


import java.util.Date;

import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.SessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.xyzq.kid.CommonTool;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/postUserInfo")
public class PostUserInfoAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {

        UserEntity userEntity = new UserEntity();
        userEntity.telephone = (String) context.get(WechatUserAjaxAction.CONTEXT_KEY_MOBILENO);
        userEntity.userName = (String)context.parameter("userName");
        userEntity.sex = (Integer)context.parameter("sex", 0);
        userEntity.address = (String)context.parameter("address", "");
        userEntity.subscribetime = CommonTool.dataToStringYMDHMS(new Date());
        userService.updateByMobileNo(userEntity);

        String telephoneNew = (String)context.parameter("telephone");
        // 修改会话中的手机号码
        String sId = visitor.cookie("sid");
        SessionEntity sessionEntity = userService.fetchSession(sId);
        sessionEntity.mobileNo = telephoneNew;
        userService.saveSession(sessionEntity);

        if(null != telephoneNew && telephoneNew.length() > 0 && !telephoneNew.equals(userEntity.telephone)) {
            //更新用户手机号
            userService.updateMobileNo(telephoneNew, userEntity.telephone);
            //更新ticket表手机号
//            ticketService.updateMobileNo(telephoneNew, userEntity.telephone);
        }
        return "success.json";
    }
}
