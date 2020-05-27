package logic.sign;

import java.util.List;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import data.bean.SevenSignCfgBean;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.sign.record.SevenDaySignRecord;
import logic.support.LogicScriptsUtils;

/***
 * 
 * N天签到管理器
 * 
 * @author lihongji
 *
 */
public class SevenDaySignManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, ICreateRoleInitialize, IAcrossDay {

    SevenDaySignRecord ssrd = new SevenDaySignRecord();

    private ISevenDaySignManagerScript getManagerScript() {
        return LogicScriptsUtils.getISevenDaySignManagerScript();
    }


    public static int SEVENDAYSIN_ID = 2;

    @Override
    public void loginInit() {

    }

    @Override
    public void createRoleInitialize() throws Exception {
        getManagerScript().createRoleInitialize(player);
    }

    /** 是否序列化 **/
    public boolean ableSerialize() {
        return getManagerScript().ableSerialize(this);
    }

    /** 是否可以领取记录 **/
    public boolean addLoginRecord() {
        return getManagerScript().addLoginRecord(this);
    }


    /** 增加记录 **/
    public void addRecord() {
        getManagerScript().addRecord(this);

    }

    /** 检测是否可以领取奖励 **/
    public boolean checkAchieve() {
        return getManagerScript().checkAchieve(this);
    }

    /** 如果7天登录的奖励id **/
    public int getSevenSignGiftId() {
        return getManagerScript().getSevenSignGiftId(this);
    }

    /** 获取最后一天的奖励 **/
    public int getSevenSignLastGiftId() {
        return getManagerScript().getSevenSignLastGiftId();
    }


    public List<SevenSignCfgBean> getSevenSignList() {
        return getManagerScript().getSevenSignList();
    }

    public ActivityInfoMsg.Builder getSignInfo() {
        return getManagerScript().getSignInfo(this);
    }

    /** 封装奖励数据 ***/
    public ActivityEntryInfoMsg.Builder packageSignInfo(int index, SevenSignCfgBean gifts, int max,
            int id, int aindex, int preindex) {
        return getManagerScript().packageSignInfo(index, gifts, max, id, aindex, preindex);
    }


    /** 获取当天签到的状态 **/
    public int getTodayStatus(int index, int aindex, int preindex) {
        return getManagerScript().getTodayStatus(index, aindex, preindex);
    }

    /**
     * 领取奖励(封装成builder返回)
     * 
     * @param builder
     */
    public ResultSubmitActivity.Builder getAward() {
        return getManagerScript().getAward(player);
    }

    public int getIndex() {
        return ssrd.getIndex();
    }

    public int getPrePreIndex() {
        return ssrd.getPreIndex();
    }


    public SevenDaySignRecord getSsrd() {
        return ssrd;
    }

    public void setSsrd(SevenDaySignRecord ssrd) {
        this.ssrd = ssrd;
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, isNotify, player);
    }

}
