package com.xyzq.kid.wechat.action.member;

import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登录确认动作
 */
@MaggieAction(path = "kid/wechat/login/route.action")
public class LoginConfirmAction extends WechatUserPageAction {
    /**
     * 派生类动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {
        Pattern pattern = Pattern.compile("kid/wechat/login/([\\w]+)");
        Matcher matcher = pattern.matcher(context.path());
        if(matcher.find()) {
            String code = matcher.group(1);
            cache.set("login-" + code, (String) context.get(WechatUserAjaxAction.CONTEXT_KEY_SID), 1000 * 60);
            context.set("url", "/kid/static/wechat/ScanResult.html?result=true");
        }
        else {
            context.set("url", "/kid/static/wechat/ScanResult.html?result=false");
        }
        return "redirect.url";
    }
}
