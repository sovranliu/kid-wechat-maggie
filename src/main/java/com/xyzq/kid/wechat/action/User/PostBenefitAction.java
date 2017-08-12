package com.xyzq.kid.wechat.action.User;

import com.google.gson.Gson;
import com.xyzq.kid.CommonTool;
import com.xyzq.kid.logic.ticket.entity.TicketEntity;
import com.xyzq.kid.logic.ticket.service.TicketService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 增票
 */
@MaggieAction(path = "kid/wechat/postBenefit")
public class PostBenefitAction extends WechatUserAjaxAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private UserService userService;

    /**
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger(PostBenefitAction.class);

    Gson gson=new Gson();
    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {
        String mobileNo = String.valueOf(context.get(CONTEXT_KEY_MOBILENO));
        logger.info("[kid/wechat/postBenefit]-in[mobileNo:" + mobileNo + "]");

        Map<String,Object> map=new HashMap<>();
        userService.readPostBenefit(mobileNo);

        return "success.json";
    }
}
