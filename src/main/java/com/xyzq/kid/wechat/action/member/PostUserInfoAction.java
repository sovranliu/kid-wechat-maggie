package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.CommonTool;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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
        userEntity.telephone = (String) context.get(PortalUserAjaxAction.CONTEXT_KEY_MOBILENO);
        userEntity.openid = (String) context.get(PortalUserAjaxAction.CONTEXT_KEY_OPENID);
        userEntity.userName = (String)context.parameter("userName");
        userEntity.sex = (Integer)context.parameter("sex", -1);
        userEntity.address = (String)context.parameter("address", "未填");
        userEntity.subscribetime = CommonTool.dataToStringYMDHMS(new Date());

        userService.insertSelective(userEntity);
        return "success.json";
    }

}
