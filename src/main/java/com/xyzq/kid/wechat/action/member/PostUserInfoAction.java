package com.xyzq.kid.wechat.action.member;

import java.util.Date;

import com.xyzq.kid.common.service.SMSService;
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
 * 修改用户信息
 */
@MaggieAction(path = "kid/wechat/postUserInfo")
public class PostUserInfoAction extends WechatUserAjaxAction {
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private SMSService smsService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {
        String telephoneOld = (String) context.get(WechatUserAjaxAction.CONTEXT_KEY_MOBILENO);
        String telephoneNew = (String) context.parameter("telephone");
        if(("" + telephoneOld).equals(telephoneNew)) {
            // 手机号码未修改
            UserEntity userEntity = new UserEntity();
            userEntity.telephone = telephoneOld;
            userEntity.userName = (String) context.parameter("userName");
            userEntity.sex = (Integer) context.parameter("sex", 0);
            userEntity.address = (String) context.parameter("address", "");
            userEntity.subscribetime = CommonTool.dataToStringYMDHMS(new Date());
            userService.updateByMobileNo(userEntity);
        }
        else {
            // 手机号码已修改
            if(null != userService.selectByMolieNo(telephoneNew)) {
                context.set("msg", "该手机号码已被注册");
                return "fail.json";
            }
            if(!smsService.checkCaptcha(telephoneNew, (String) context.parameter("code"))) {
                context.set("msg", "验证码不正确");
                return "fail.json";
            }
            UserEntity userEntity = new UserEntity();
            userEntity.telephone = telephoneOld;
            userEntity.userName = (String) context.parameter("userName");
            userEntity.sex = (Integer) context.parameter("sex", 0);
            userEntity.address = (String) context.parameter("address", "");
            userEntity.subscribetime = CommonTool.dataToStringYMDHMS(new Date());
            userService.updateByMobileNo(userEntity);
            // 更新用户手机号
            userService.updateMobileNo(telephoneNew, telephoneOld);
            // 修改会话中的手机号码
            String sId = visitor.cookie("sid");
            SessionEntity sessionEntity = userService.fetchSession(sId);
            sessionEntity.mobileNo = telephoneNew;
            userService.saveSession(sessionEntity);
        }
        return "success.json";
    }
}
