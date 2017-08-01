package com.xyzq.kid.wechat.action;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.etc.Serial;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * 微信跳转中间页
 */
@MaggieAction(path = "kid/wechat/jump")
public class WechatJumpAction implements IAction {
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
        String url = (String) context.parameter("url");
        String code = (String) context.parameter("code");
        String openId = WebHelper.fetchOpenId(code);
        // 通过OpenID获取用户实体
        UserEntity userEntity = userService.selectByOpenId(openId);
        String mobileNo = null;
        if(null != userEntity) {
            mobileNo = userEntity.mobileno;
        }
        if(null == mobileNo) {
            context.put("location", url_page_register + "?openId=" + openId);
            return "302.code";
        }
        String sId = Serial.makeLocalID();
        cache.set("sid-" + sId, mobileNo + "," + openId);
        visitor.setCookie("sid", sId);
        context.put("location", url);
        return "302.code";
    }

    /**
     * 字符串转URL
     */
    public static String string2Url(String string) throws UnsupportedEncodingException {
        byte[] bytes = hexStringToBytes(string);
        return new String(bytes, "utf-8");
    }
    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
