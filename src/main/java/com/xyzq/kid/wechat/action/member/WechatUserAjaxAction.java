package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 注册会员Ajax基类动作
 *
 * 继承此类的回调确保为可以从context上获取openId
 */
public class WechatUserAjaxAction implements IAction {
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
     * 缓存访问对象
     *
     * 缓存中内容为：mobileNo,openId
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
            String mobileNoOpenId = cache.get("sid-" + sId);
            if(!Text.isBlank(mobileNoOpenId)) {
                String mobileNo = mobileNoOpenId.split(",")[0].trim();
                String openId = mobileNoOpenId.split(",")[1].trim();
                context.put(CONTEXT_KEY_MOBILENO, mobileNo);
                context.put(CONTEXT_KEY_OPENID, openId);
                context.put(CONTEXT_KEY_SID, sId);
                return null;
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
        return "success.json";
    }

    /**
     * URL转字符串
     */
    public static String url2String(String url) throws UnsupportedEncodingException {
        return bytesToHexString(url.getBytes("utf-8"));
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
