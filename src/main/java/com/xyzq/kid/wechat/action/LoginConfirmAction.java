package com.xyzq.kid.wechat.action;

import com.xyzq.simpson.base.etc.Base64;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登录确认动作
 */
@MaggieAction(path = "kid/wechat/login/route.action")
public class LoginConfirmAction extends WechatUserAction {
    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String result = super.execute(visitor, context);
        if(null != result) {
            return result;
        }
        Pattern pattern = Pattern.compile("kid/wechat/login/([\\w=]+)");
        Matcher matcher = pattern.matcher(context.path());
        if(matcher.find()) {
            String code = matcher.group(1);
            cache.set("login-" + code, (String) context.get(WechatUserAction.CONTEXT_KEY_SID), 1000 * 60);
            return "success.json";
        }
        return "fail.json";
    }
}
