package logic.sign;

import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.sign.record.TomorrowSignRecord;
import logic.support.LogicScriptsUtils;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;

/***
 * 次日登陆管理器
 * 
 * @author lihongji
 *
 */
public class TomorrowSignManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IAcrossDay {

    // 次日登录记录
    TomorrowSignRecord tsrd = new TomorrowSignRecord();


    public static int TOMORROWSIGN_ID = 3;


    private ISignScript getManagerScript() {
        return LogicScriptsUtils.getITomorrowSignManagerScript();
    }

    /** 跨天 **/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, getPlayer());
    }


    /** 是否序列化 **/
    public boolean ableSerialize() {
        return getManagerScript().ableSerialize(getPlayer());
    }

    /**
     * 领取奖励(封装成builder返回)
     * 
     * @param builder
     */
    public ResultSubmitActivity.Builder getAward() {
        return getManagerScript().getAward(player);
    }

    /** 是否可以领取奖励 **/
    public boolean checkAchieve() {
        return getManagerScript().checkAchieve(getPlayer());
    }

    public Builder getSignInfo() {
        return getManagerScript().getSignInfo(getPlayer());
    }


    public TomorrowSignRecord getTsrd() {
        return tsrd;
    }

    public void setTsrd(TomorrowSignRecord tsrd) {
        this.tsrd = tsrd;
    }

}
