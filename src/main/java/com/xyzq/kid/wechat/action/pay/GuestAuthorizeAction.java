package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;

/**
 * 微信授权动作
 */
@MaggieAction(path = "kid/wechat/authorize")
public class GuestAuthorizeAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(GuestAuthorizeAction.class);
    /**
     * 站点域名
     */
    @Value("${KID.URL_DOMAIN}")
    public String url_domain;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String referer = context.header().get("Referer");
        if(Text.isBlank(referer)) {
            logger.error("unexpected wechat authorize request referer is blank");
            context.set("msg", "微信授权请求所在页面地址为空");
            return "fail.json";
        }
        String url = URLEncoder.encode(referer, "utf-8");
        String jumpUrl = URLEncoder.encode(url_domain + "/kid/wechat/jump/guest?url=" + url, "utf-8");
        String redirectUri = WebHelper.URL_AUTHORIZE.replace("[REDIRECT_URI]", jumpUrl).replace("[STATE]", "kid");
        context.set("redirect", redirectUri);
        return "success.json";
    }
}
