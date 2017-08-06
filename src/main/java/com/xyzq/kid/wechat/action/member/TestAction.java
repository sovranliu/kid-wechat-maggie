package com.xyzq.kid.wechat.action.member;

import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import com.xyzq.simpson.utility.cache.core.ITimeLimitedCache;

import javax.annotation.Resource;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/test")
public class TestAction implements IAction {
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
        cache.set("sid-tester", "15021819287,ovQHwwFwTV4stCe3ncywvDrkDioI");
        visitor.setCookie("sid", "tester");
        context.set("msg", "测试连接，用户手机号码15021819287");
        return "success.json";
    }
}
