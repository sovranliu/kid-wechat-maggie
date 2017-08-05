package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.kid.logic.user.entity.SessionEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.net.URLEncoder;

/**
 * 会员Ajax基类动作
 *
 * 继承此类的回调确保为可以从context上获取openId
 */
public abstract class WechatUserAjaxAction implements IAction {
    /**
     * 上下文中的键
     */
    public final static String CONTEXT_KEY_SID = "sid";
    /**
     * 手机号码在上下文中的键
     */
    public final static String CONTEXT_KEY_MOBILENO = "mobileNo";
    /**
     * 用户微信开放ID在上下文中的键
     */
    public final static String CONTEXT_KEY_OPENID = "openId";

    /**
     * 站点域名
     */
    @Value("${KID.URL_DOMAIN}")
    public String url_domain;
    @Value("${KID.URL_PAGE_DEFAULT}")
    public String url_page_default;
    @Value("${KID.URL_PAGE_REGISTER}")
    public String url_page_register;
    /**
     * 用户服务
     */
    @Autowired
    protected UserService userService;
    /**
     * 缓存访问对象
     */
    @Resource(name = "cache")
    protected ITimeLimitedCache<String, String> cache;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String sId = visitor.cookie("sid");
        if(!Text.isBlank(sId)) {
            SessionEntity sessionEntity = userService.fetchSession(sId);
            if(null != sessionEntity) {
                context.put(CONTEXT_KEY_MOBILENO, sessionEntity.mobileNo);
                context.put(CONTEXT_KEY_OPENID, sessionEntity.openId);
                context.put(CONTEXT_KEY_SID, sId);
                return doExecute(visitor, context);
            }
        }
        String referer = context.header().get("Referer");
        if(null == referer) {
            referer = url_page_default;
        }
        String url = URLEncoder.encode(referer, "utf-8");
        String jumpUrl = URLEncoder.encode(url_domain + "/kid/wechat/jump/member?url=" + url, "utf-8");
        String redirectUri = WebHelper.URL_AUTHORIZE.replace("[REDIRECT_URI]", jumpUrl).replace("[STATE]", "kid");
        context.put("redirect", redirectUri);
        return "fail.json";
    }

    /**
     * 派生类动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    public abstract String doExecute(Visitor visitor, Context context) throws Exception;
}
