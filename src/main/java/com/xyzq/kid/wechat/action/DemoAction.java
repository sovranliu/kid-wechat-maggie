package com.xyzq.kid.wechat.action;

import com.xyzq.kid.logic.service.DemoService;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 范例动作
 */
@MaggieAction(path = "kid/wechat/demo")
public class DemoAction implements IAction {
    /**
     * Action中只支持Autowired注解引入SpringBean
     */
    @Autowired
    private DemoService demoService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        context.set("msg", "这个是前端需要展示的消息");
        context.set("data", JSONObject.convertFromObject(demoService.call()).toString());
        return "success.json";
    }
}
