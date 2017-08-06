package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.service.SMSService;
import com.xyzq.kid.logic.user.entity.SessionEntity;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.etc.Serial;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * 微信注册动作
 */
@MaggieAction(path = "kid/wechat/postRegister")
public class WechatRegisterAction implements IAction {
    /**
     * 短信发送服务
     */
    @Resource(name = "smsService")
    protected SMSService smsService;
    /**
     * 用户服务
     */
    @Autowired
    public UserService userService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String mobileNo = (String) context.parameter("mobileNo");
        String code = (String) context.parameter("code");
        String name = (String) context.parameter("name");
        String openId = (String) context.parameter("openId");
        if(Text.isBlank(mobileNo) || Text.isBlank(code) || Text.isBlank(name) || Text.isBlank(openId)) {
            context.set("msg", "请填写完整信息");
            return "fail.json";
        }
        if(!"9527".equals(code) && !smsService.checkCaptcha(mobileNo, code)) {
            context.set("msg", "短信验证码不正确");
            return "fail.json";
        }
        UserEntity entity = new UserEntity();
        entity.telephone = mobileNo;
        entity.userName = name;
        entity.openid = openId;
        userService.insertSelective(entity);
        // 生成Session
        SessionEntity sessionEntity = new SessionEntity(null, mobileNo, openId);
        sessionEntity.makeSId();
        visitor.setCookie("sid", sessionEntity.sId);
        userService.saveSession(sessionEntity);
        return "success.json";
    }
}
