package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.service.SMSService;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;

import javax.annotation.Resource;

/**
 * 发送短信动作
 */
@MaggieAction(path = "kid/wechat/getVerificationCode")
public class SendSMSAction implements IAction {
    /**
     * 缓存访问对象
     *
     * 缓存中内容为：code-15021819287 -> 9527
     */
    @Resource(name = "cache")
    protected ITimeLimitedCache<String, String> cache;
    /**
     * 短信发送服务
     */
    @Resource(name = "smsService")
    protected SMSService smsService;


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
        smsService.sendSMSCaptcha(mobileNo, "register", null);
        return "success.json";
    }
}
