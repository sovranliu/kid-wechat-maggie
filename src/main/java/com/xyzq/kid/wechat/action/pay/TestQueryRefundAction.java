package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.finance.service.RefundService;
import com.xyzq.kid.finance.service.entity.RefundEntity;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 退款测试动作
 */
@MaggieAction(path = "kid/wechat/testQueryRefund")
public class TestQueryRefundAction implements IAction {
    /**
     * 退款服务
     */
    @Autowired
    private RefundService refundService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        RefundEntity refundEntity = refundService.queryRefund((String) context.parameter("orderNo"));
        context.set("data", refundEntity.state);
        return "success.json";
    }
}
