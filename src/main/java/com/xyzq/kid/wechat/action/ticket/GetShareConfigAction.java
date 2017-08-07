package com.xyzq.kid.wechat.action.ticket;

import com.xyzq.kid.common.wechat.mp.JSConfig;
import com.xyzq.kid.common.wechat.mp.JSHelper;
import com.xyzq.kid.wechat.action.member.WechatUserAjaxAction;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.base.json.JSONString;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 飞行票分享动作
 */
@MaggieAction(path = "kid/wechat/getShareConfig")
public class GetShareConfigAction extends WechatUserAjaxAction {
    /**
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger(GetShareConfigAction.class);

    /**
     * 派生类动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String doExecute(Visitor visitor, Context context) throws Exception {
        String referer = context.header().get("Referer");
        if(null == referer) {
            referer = context.header().get("referer");
        }
        if(null == referer) {
            context.set("msg", "网页Referer意外为空");
            return "fail.json";
        }
        logger.info("[kid/wechat/getShareConfig]-in:" + referer);
        JSConfig jsConfig = JSHelper.fetchConfig(referer);
        JSONObject json = new JSONObject();
        json.put("appId", new JSONString(jsConfig.appId));
        json.put("timestamp", new JSONString(jsConfig.timestamp));
        json.put("nonceStr", new JSONString(jsConfig.nonceString));
        json.put("signature", new JSONString(jsConfig.signature));
        context.set("data", json);
        logger.info("[kid/wechat/getShareConfig]-json:" + json);

        return "success.json";
    }
}
