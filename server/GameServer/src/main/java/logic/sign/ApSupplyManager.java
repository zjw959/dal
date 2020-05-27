package logic.sign;

import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.sign.record.ApSupplyRecord;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;

/***
 * 
 * 体力补给
 * 
 * @author lihongji
 *
 */
public class ApSupplyManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter,ITick,ICreatePlayerInitialize {

    /** 体力补给id **/
    public static int APSUPPLY_ID = 4;

    ApSupplyRecord asrd = new ApSupplyRecord();

    private ISignScript getManagerScript() {
        return LogicScriptsUtils.getIApSupplyManagerScript();
    }

    /*
     * 领取奖励(封装成builder返回)
     * 
     * @param builder
     */
    public ResultSubmitActivity.Builder getAward() {
        return getManagerScript().getAward(player);
    }


    /***
     * 获取当前体力补给的信息
     * 
     * @return
     */
    public Builder getSignInfo() {
        return getManagerScript().getSignInfo(getPlayer());
    }


    /** 刷新体力补给信息 **/
    public void refreshEnery() {
        RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
        respBuilder.addActivitys(getSignInfo());
        MessageUtils.send(getPlayer(), respBuilder);

    }

    public ApSupplyRecord getAsrd() {
        return asrd;
    }

    public void setAsrd(ApSupplyRecord asrd) {
        this.asrd = asrd;
    }

    //检测体力活动
    @Override
    public void tick() {
        ApSupplyContainer.getInstance().tick();
    }

    /**初始化**/
    @Override
    public void createPlayerInitialize() {
       ApSupplyContainer.getInstance().tick();
    }

}
