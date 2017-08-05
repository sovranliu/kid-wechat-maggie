package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.kid.logic.user.entity.SessionEntity;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 微信跳转中间页
 */
@MaggieAction(path = "kid/wechat/jump/member")
public class MemberJumpAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(MemberJumpAction.class);
    /**
     * 注册页
     */
    @Value("${KID.URL_PAGE_REGISTER}")
    public String url_page_register;
    /**
     * 用户服务
     */
    @Autowired
    protected UserService userService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String url = (String) context.parameter("url");
        if(Text.isBlank(url)) {
            logger.error("guest jump with blank url");
            context.set("content", "出错了，客户跳转地址为空");
            return "text.txt";
        }
        String code = (String) context.parameter("code");
        if(Text.isBlank(code)) {
            logger.error("guest jump with blank code");
            context.set("content", "出错了，客户授权失败");
            return "text.txt";
        }
        String openId = WebHelper.fetchOpenId(code);
        if(Text.isBlank(openId)) {
            logger.error("fetch open id with code '" + code + "' failed");
            context.set("content", "出错了，调用微信服务器获取用户ID失败");
            return "text.txt";
        }
        // 通过微信用户开放ID获取用户实体
        UserEntity userEntity = userService.selectByOpenId(openId);
        String mobileNo = null;
        if(null != userEntity) {
            mobileNo = userEntity.mobileno;
        }
        if(null == mobileNo) {
            context.put("location", url_page_register + "?openId=" + openId);
            return "302.code";
        }
        SessionEntity sessionEntity = new SessionEntity(null, mobileNo, openId);
        sessionEntity.makeSId();
        userService.saveSession(sessionEntity);
        visitor.setCookie("sid", sessionEntity.sId);
        context.put("location", url);
        return "302.code";
    }
}
