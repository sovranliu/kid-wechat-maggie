package com.xyzq.kid.wechat.action.pay;

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

import java.net.URLEncoder;

/**
 * 预支付动作
 *
 * 重定向页面使得页面url具有购买者mobileNo和支付者openId两个参数
 */
@MaggieAction(path = "kid/wechat/prepay")
public class PrepayAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(PrepayAction.class);
    /**
     * 站点域名
     */
    @Value("${KID.URL_DOMAIN}")
    public String url_domain;
    /**
     * 注册页面地址
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
        // 提取当前支付页面URL
        String referer = context.header().get("Referer");
        if(null == referer) {
            referer = context.header().get("referer");
        }
        if(Text.isBlank(referer)) {
            logger.error("unexpected wechat authorize request referer is blank");
            context.set("msg", "微信授权请求所在页面地址为空");
            return "fail.json";
        }
        // 抽取页面URL中的mobileNo和openId，并删除URL中的这两个参数
        String mobileNo = Text.substring(referer + "&", "mobileNo=", "&");
        if(Text.isBlank(mobileNo)) {
            referer = referer.replace("mobileNo=", "");
        }
        String openId = Text.substring(referer + "&", "openId=", "&");
        if(Text.isBlank(openId)) {
            referer = referer.replace("openId=", "");
        }
        referer = referer.replace("?&", "?").trim();
        referer = referer.replace("&&", "&").trim();
        if(referer.endsWith("?")) {
            referer = referer.substring(0, referer.length() - 1);
        }
        if(!Text.isBlank(mobileNo) && !Text.isBlank(openId)) {
            // 页面自身不需要重定向
            return "success.json";
        }
        String sId = visitor.cookie("sid");
        if(null != sId) {
            // 尝试以登录用户身份重定向
            SessionEntity sessionEntity = userService.fetchSession(sId);
            if(null != sessionEntity) {
                if(Text.isBlank(mobileNo)) {
                    mobileNo = sessionEntity.mobileNo;
                }
                if(Text.isBlank(openId)) {
                    openId = sessionEntity.openId;
                }
                if(referer.contains("?")) {
                    referer = referer + "&mobileNo=" + mobileNo + "&openId=" + openId;
                }
                else {
                    referer = referer + "?mobileNo=" + mobileNo + "&openId=" + openId;
                }
                context.set("redirect", referer);
                return "success.json";
            }
            logger.error("prepay find invalid session, sid = " + sId);
        }
        if(Text.isBlank(mobileNo)) {
            // 手机号码不存在则通过微信用户开放ID查询当前微信用户手机号码
            UserEntity userEntity = userService.selectByOpenId(openId);
            if(null == userEntity) {
                logger.error("unregister openId '" + openId + "' when prepay");
                context.set("msg", "请先注册");
                context.set("redirect", url_page_register);
                return "success.json";
            }
            mobileNo = userEntity.telephone;
            if(referer.contains("?")) {
                referer = referer + "&mobileNo=" + mobileNo + "&openId=" + openId;
            }
            else {
                referer = referer + "?mobileNo=" + mobileNo + "&openId=" + openId;
            }
            context.set("redirect", referer);
            return "success.json";
        }
        // 尝试通过重定向获取微信用户开放ID
        if(referer.contains("?")) {
            referer = referer + "&mobileNo=" + mobileNo;
        }
        else {
            referer = referer + "?mobileNo=" + mobileNo;
        }
        String url = URLEncoder.encode(referer, "utf-8");
        String jumpUrl = URLEncoder.encode(url_domain + "/kid/wechat/jump/guest?url=" + url, "utf-8");
        String redirectUri = WebHelper.URL_AUTHORIZE.replace("[REDIRECT_URI]", jumpUrl).replace("[STATE]", "kid");
        context.set("redirect", redirectUri);
        return "success.json";
    }
}
