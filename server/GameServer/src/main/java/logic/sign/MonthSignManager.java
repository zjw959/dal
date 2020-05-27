package logic.sign;

import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity.Builder;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.sign.record.MonthSignRecord;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 
 * 月签到管理器
 * 
 * @author lihongji
 *
 */
public class MonthSignManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IAcrossDay, ICreateRoleInitialize {

    /** 月签到记录器 **/
    MonthSignRecord msrd = new MonthSignRecord();

    /** 月签到id **/
    public static final int MONTHSIGN_ID = 1;


    private ISignScript getManagerScript() {
        return LogicScriptsUtils.getIMonthSignManagerScript();
    }


    /**** 跨天的时候需要更新客户端的月签到信息 ****/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, player);
    }

    /** 获取签到信息 **/
    public org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder getSignInfo() {
        return getManagerScript().getSignInfo(getPlayer());
    }

    /** 领取奖励 **/
    public Builder getAward() {
        return getManagerScript().getAward(getPlayer());
    }

    public MonthSignRecord getMsrd() {
        return msrd;
    }

    public void setMsrd(MonthSignRecord msrd) {
        this.msrd = msrd;
    }

    @Override
    public void createRoleInitialize() throws Exception {
        getManagerScript().createRoleInitialize(getPlayer());
    }

}
