package com.xyzq.kid.wechat.action;

import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;

/**
 * 微信握手动作
 *
 * 用于纯静态页面判断用户是否是注册用户
 */
@MaggieAction(path = "kid/wechat/handshake")
public class HandShakeAction extends WechatUserAction {
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
        return "success.json";
    }
}
