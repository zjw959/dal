package logic.sign;

import java.util.List;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import data.bean.SevenSignCfgBean;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import script.IScript;

public interface ISevenDaySignManagerScript extends IScript {

    public void createRoleInitialize(Player player);

    /** 是否序列化 **/
    public boolean ableSerialize(SevenDaySignManager sevenDaySignManager);

    /** 是否可以领取记录 **/
    public boolean addLoginRecord(SevenDaySignManager sevenDaySignManager);

    /** 检测是否可以领取奖励 **/
    public boolean checkAchieve(SevenDaySignManager sevenDaySignManager);

    /** 如果7天登录的奖励id **/
    public int getSevenSignGiftId(SevenDaySignManager sevenDaySignManager);

    /** 获取最后一天的奖励 **/
    public int getSevenSignLastGiftId();

    public List<SevenSignCfgBean> getSevenSignList();

    public ActivityInfoMsg.Builder getSignInfo(SevenDaySignManager sevenDaySignManager);

    /** 封装奖励数据 ***/
    public ActivityEntryInfoMsg.Builder packageSignInfo(int index, SevenSignCfgBean gifts, int max,
            int id, int aindex, int preindex);

    /** 获取当天签到的状态 **/
    public int getTodayStatus(int index, int aindex, int preindex);

    /**
     * 领取奖励(封装成builder返回)
     * 
     * @param builder
     */
    public ResultSubmitActivity.Builder getAward(Player player);

    public void acrossDay(EAcrossDayType type, boolean isNotify, Player player);

    /** 增加记录 **/
    public void addRecord(SevenDaySignManager sevenDaySignManager);


}
