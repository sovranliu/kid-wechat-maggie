package com.xyzq.kid.wechat.action.member;

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
     * 缓存访问对象
     *
     * 缓存中内容为：mobileNo,openId
     */
    @Resource(name = "cache")
    protected ITimeLimitedCache<String, String> cache;
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
        String mobileNo = (String) context.parameter("mobileNumber");
        String code = (String) context.parameter("code");
        String name = (String) context.parameter("name");
        String openId = (String) context.parameter("openId");
        if(Text.isBlank(mobileNo) || Text.isBlank(code) || Text.isBlank(name) || Text.isBlank(openId)) {
            context.set("msg", "请填写完整信息");
            return "fail.json";
        }
        if(code.equalsIgnoreCase(cache.get("code-" + mobileNo))) {
            context.set("msg", "短信验证码不正确");
            return "fail.json";
        }
        UserEntity entity = new UserEntity();
        entity.mobileno = mobileNo;
        entity.realname = name;
        entity.openid = openId;
        userService.insertSelective(entity);
        visitor.setCookie("sid", makeSId(mobileNo, openId));
        return "success.json";
    }

    /**
     * 构建会话
     *
     * @param mobileNo 手机号码
     * @param openId 微信用户开放ID
     * @return 用户会话ID
     */
    public String makeSId(String mobileNo, String openId) {
        String sId = Serial.makeLocalID();
        cache.set("sid-" + sId, mobileNo + "," + openId);
        return sId;
    }
}
