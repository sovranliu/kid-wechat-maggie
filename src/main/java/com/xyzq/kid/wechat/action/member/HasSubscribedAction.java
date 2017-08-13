package com.xyzq.kid.wechat.action.member;

import com.xyzq.kid.common.wechat.mp.UserInfoHelper;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;

/**
 * 判断指定微信开放ID是否是公众号关注者动作
 */
@MaggieAction(path = "kid/wechat/hasSubscribed")
public class HasSubscribedAction implements IAction {
    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String openId = (String) context.parameter("openId");
        if(Text.isBlank(openId)) {
            context.set("msg", "OpenID不能为空");
            return "fail.json";
        }
        context.set("data", UserInfoHelper.isFans(openId));
        return "success.json";
    }
}
