package javascript.logic.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import data.GameDataManager;
import data.bean.SevenSignCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.sign.ISevenDaySignManagerScript;
import logic.sign.SevenDaySignManager;
import logic.support.MessageUtils;
import utils.CommonUtil;

public class SevenDaySignManagerScript implements ISevenDaySignManagerScript {


    @Override
    public void createRoleInitialize(Player player) {
        if (ableSerialize(player.getSevenDaySignManager())
                && addLoginRecord(player.getSevenDaySignManager())) {
            RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
            respBuilder.addActivitys(getSignInfo(player.getSevenDaySignManager()));
            MessageUtils.send(player, respBuilder);
        }
    }

    /** 是否序列化 **/
    public boolean ableSerialize(SevenDaySignManager sevenDaySignManager) {
        if (sevenDaySignManager.getSsrd().getEndTime() >= System.currentTimeMillis()
                && sevenDaySignManager.getPrePreIndex()
                        + sevenDaySignManager.getIndex() >= getSevenSignList().size())
            return false;
        return true;
    }

    /** 是否可以领取记录 **/
    @Override
    public boolean addLoginRecord(SevenDaySignManager sevenDaySignManager) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.SEVEN_GIFT)) {
            return false;
        }
        if (sevenDaySignManager.getPrePreIndex()
                + sevenDaySignManager.getIndex() < getSevenSignList().size())
            sevenDaySignManager.getSsrd().addMayAchieveRecord();
        return true;
    }


    /** 增加记录 **/
    public void addRecord(SevenDaySignManager sevenDaySignManager) {
        sevenDaySignManager.getSsrd().addAchieveRecord(sevenDaySignManager.getSsrd().getIndex()
                + sevenDaySignManager.getSsrd().getPreIndex() == getSevenSignLastGiftId());
    }

    /** 检测是否可以领取奖励 **/
    public boolean checkAchieve(SevenDaySignManager sevenDaySignManager) {
        return sevenDaySignManager.getSsrd().getPreIndex() == 0 ? false : true;
    }

    /** 如果7天登录的奖励id **/
    public int getSevenSignGiftId(SevenDaySignManager sevenDaySignManager) {
        if (sevenDaySignManager.getSsrd().getIndex() != 0)
            return sevenDaySignManager.getSsrd().getIndex();
        List<SevenSignCfgBean> signList = GameDataManager.getSevenSignCfgBeans();
        return signList.get(0).getId();
    }

    /** 获取最后一天的奖励 **/
    @Override
    public int getSevenSignLastGiftId() {
        List<SevenSignCfgBean> signList = GameDataManager.getSevenSignCfgBeans();
        return signList.get(signList.size() - 1).getId();
    }


    public List<SevenSignCfgBean> getSevenSignList() {
        return GameDataManager.getSevenSignCfgBeans();
    }

    public ActivityInfoMsg.Builder getSignInfo(SevenDaySignManager sevenDaySignManager) {
        int index = sevenDaySignManager.getIndex();
        int preIndex = sevenDaySignManager.getPrePreIndex();
        ActivityInfoMsg.Builder builder = ActivityInfoMsg.newBuilder();
        builder.setCt(ChangeType.DEFAULT);
        builder.setId(SevenDaySignManager.SEVENDAYSIN_ID);
        builder.setStartTime((int) (System.currentTimeMillis() / 1000));
        builder.setEndTime(sevenDaySignManager.getSsrd().getEndTime() == 0
                ? (int) ( (System.currentTimeMillis() / 1000) + 3600 * 120)
                : (int) (sevenDaySignManager.getSsrd().getEndTime()/ 1000));
        builder.setActivityType(SevenDaySignManager.SEVENDAYSIN_ID);
        builder.setResetType(0);
        com.alibaba.fastjson.JSONObject json = new JSONObject();
        builder.setRank(0);
        // 奖励信息

        json.put("title", GameDataManager
                .getStringCfgBean(
                        Integer.parseInt(GameDataManager.getSevenSignCfgBeans().get(0).getName()))
                .getText());
        String bg = null;
        int i = 0;
        for (SevenSignCfgBean sign : getSevenSignList()) {
            i++;
            if (bg == null && sign.getAdIcon() != null && sign.getAdIcon().length() > 0)
                bg = sign.getAdIcon();
            builder.addEntrys(
                    packageSignInfo(i, sign, getSevenSignList().size(), 1, index, preIndex));
        }
        json.put("bg", bg);
        builder.setRemark(json.toJSONString());
        return builder;
    }

    /** 封装奖励数据 ***/
    public ActivityEntryInfoMsg.Builder packageSignInfo(int index, SevenSignCfgBean gifts, int max,
            int id, int aindex, int preindex) {
        ActivityEntryInfoMsg.Builder entryBuilder = ActivityEntryInfoMsg.newBuilder();
        com.alibaba.fastjson.JSONObject json = new JSONObject();
        json.put("title", "第" + index + "天");
        entryBuilder.setId(SevenDaySignManager.SEVENDAYSIN_ID);
        entryBuilder.setNowProgress(index);
        entryBuilder.setMaxProgress(max);
        entryBuilder.setStatus(getTodayStatus(index, aindex, preindex));
        entryBuilder.setRemark(json.toJSONString());
        entryBuilder.setReward(JSON.toJSONString(gifts.getGifts()));
        return entryBuilder.setRank(index);
    }


    /** 获取当天签到的状态 **/
    @Override
    public int getTodayStatus(int index, int aindex, int preindex) {
        if (index <= aindex)
            return 2;
        if (index <= aindex + preindex)
            return 1;
        return 0;
    }

    /**
     * 领取奖励(封装成builder返回)
     * 
     * @param builder
     */
    @SuppressWarnings("unchecked")
    public ResultSubmitActivity.Builder getAward(Player player) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.SEVEN_GIFT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:seven_gift");
        }
        ResultSubmitActivity.Builder builder = ResultSubmitActivity.newBuilder();
        // 检测是否可以领取奖励
        if (!checkAchieve(player.getSevenDaySignManager()))
            MessageUtils.throwCondtionError(GameErrorCode.TODAY_SIGNED);
        // 封装数据
        builder.setActivityid(SevenDaySignManager.SEVENDAYSIN_ID);
        List<RewardsMsg> rewardsMsgs = Lists.newArrayList();
        BagManager bm = player.getBagManager();
        Map<Integer, Integer> award = new HashMap<Integer, Integer>();
        for (int i = 0; i < player.getSevenDaySignManager().getPrePreIndex(); i++) {
            Map<Integer, Integer> giftMap = getSevenSignList()
                    .get(player.getSevenDaySignManager().getIndex() + i).getGifts();
            for (Map.Entry<Integer, Integer> pathEntry : giftMap.entrySet()) {
                CommonUtil.changeMap(award, pathEntry.getKey(), pathEntry.getValue());
                rewardsMsgs.add(RewardsMsg.newBuilder().setId(pathEntry.getKey())
                        .setNum(pathEntry.getValue()).build());
            }
        }
        // 重置记录
        addRecord(player.getSevenDaySignManager()); 
        // 增加奖励
        bm.addItems(award, true, EReason.SEVEN_SIGN);
        builder.addAllRewards(rewardsMsgs);
        return builder;
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify, Player player) {
        // 0点跨天
        if (type == EAcrossDayType.SYS_ACROSS_DAY) {
            // 功能是否关闭
            if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.SEVEN_GIFT)) {
                return;
            }
            if (ableSerialize(player.getSevenDaySignManager())
                    && addLoginRecord(player.getSevenDaySignManager())) {
                RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
                respBuilder.addActivitys(getSignInfo(player.getSevenDaySignManager()));
                MessageUtils.send(player, respBuilder);
            }
        }
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.SEVEN_MANAGER_SCRIPT.Value();
    }

}
