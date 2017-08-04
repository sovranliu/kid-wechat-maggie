package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.common.wechat.mp.WebHelper;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游客跳转中间页
 *
 * 用于游客带着openId重定向回之前的页面
 */
@MaggieAction(path = "kid/wechat/jump/guest")
public class GuestJumpAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(GuestJumpAction.class);


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
        if(Text.isBlank(url)) {
            logger.error("guest jump with blank url");
            context.set("content", "出错了，客户跳转地址为空");
            return "text.txt";
        }
        String code = (String) context.parameter("code");
        if(Text.isBlank(code)) {
            logger.error("guest jump with blank code");
            context.set("content", "出错了，客户授权失败");
            return "text.txt";
        }
        String openId = WebHelper.fetchOpenId(code);
        if(Text.isBlank(openId)) {
            logger.error("fetch open id with code '" + code + "' failed");
            context.set("content", "出错了，调用微信服务器获取用户ID失败");
            return "text.txt";
        }
        if(url.contains("?")) {
            url += "&openId=" + openId;
        }
        else {
            url += "?openId=" + openId;
        }
        context.put("location", url);
        return "302.code";
    }
}
