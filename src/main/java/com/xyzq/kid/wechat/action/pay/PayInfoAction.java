package com.xyzq.kid.wechat.action.pay;

import com.xyzq.kid.logic.config.common.ConfigCommon;
import com.xyzq.kid.logic.config.service.ConfigService;
import com.xyzq.kid.logic.user.entity.UserEntity;
import com.xyzq.kid.logic.user.service.UserService;
import com.xyzq.simpson.base.json.JSONNumber;
import com.xyzq.simpson.base.json.JSONObject;
import com.xyzq.simpson.base.json.JSONString;
import com.xyzq.simpson.base.text.Text;
import com.xyzq.simpson.maggie.access.spring.MaggieAction;
import com.xyzq.simpson.maggie.framework.Context;
import com.xyzq.simpson.maggie.framework.Visitor;
import com.xyzq.simpson.maggie.framework.action.core.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付信息查询动作
 *
 * 支付页通过此动作获取购买者和商品相关信息
 */
@MaggieAction(path = "kid/wechat/payInfo")
public class PayInfoAction implements IAction {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(PayInfoAction.class);
    /**
     * 用户服务
     */
    @Autowired
    protected UserService userService;
    /**
     * 参数服务
     */
    @Autowired
    protected ConfigService configService;


    /**
     * 动作执行
     *
     * @param visitor 访问者
     * @param context 请求上下文
     * @return 下一步动作，包括后缀名，null表示结束
     */
    @Override
    public String execute(Visitor visitor, Context context) throws Exception {
        String payerOpenId = (String) context.get("openId");
        String ownerMobileNo = (String) context.get("mobileNo");
        if(Text.isBlank(payerOpenId) || Text.isBlank(ownerMobileNo)) {
            context.set("msg", "商品购买查询信息不全");
            return "fail.json";
        }
        UserEntity userEntity = userService.selectByMolieNo(ownerMobileNo);
        if(null == userEntity) {
            context.set("msg", "购买者帐号不存在");
            return "fail.json";
        }
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("mobileNo", new JSONString(ownerMobileNo));
        jsonUser.put("name", new JSONString("" + userEntity.realname));
        JSONObject feeUser = new JSONObject();
        feeUser.put("singleticket", new JSONNumber(configService.fetch(ConfigCommon.FEE_SINGLETICKET, Integer.class)));
        feeUser.put("groupticket", new JSONNumber(configService.fetch(ConfigCommon.FEE_GROUPTICKET, Integer.class)));
        feeUser.put("insurance", new JSONNumber(configService.fetch(ConfigCommon.FEE_INSURANCE, Integer.class)));
        feeUser.put("record", new JSONNumber(configService.fetch(ConfigCommon.FEE_RECORD, Integer.class)));
        JSONObject data = new JSONObject();
        data.put("user", jsonUser);
        data.put("fee", feeUser);
        context.set("data", data);
        return "success.json";
    }
}
