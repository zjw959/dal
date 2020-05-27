package logic.sign;

import java.util.List;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import script.IScript;

/***
 * 签到
 * 
 * @author lihongji
 *
 */
public abstract class ISignScript implements IScript {

    /** 增加记录 **/
    public abstract void addRecord(Player player, int param);

    /** 检测是能够领取奖励 **/
    public abstract boolean checkAchieve(Player player);

    /** 获取奖励 **/
    public abstract ResultSubmitActivity.Builder getAward(Player player);

    /** 获取sign信息 **/
    public abstract org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder getSignInfo(
            Player player);

    /** 获取配置信息 **/
    @SuppressWarnings("rawtypes")
    public abstract List getGift();

    /** 获取记录 **/
    public abstract int getSignRecord(Player player);

    /** 查看奖励状态 **/
    public abstract int getAwardStatus(Player player, int index);

    /** 咵天 **/
    public abstract void acrossDay(EAcrossDayType type, Player player);

    /** 是否序列化 **/
    public abstract boolean ableSerialize(Player player);

    public abstract void createRoleInitialize(Player player);


}
