package javascript.logic.sign;

import java.util.List;
import java.util.Map;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity.Builder;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import data.GameDataManager;
import data.bean.TomorrowSignCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.sign.ISignScript;
import logic.sign.TomorrowSignManager;
import logic.sign.record.TomorrowSignRecord;
import logic.support.MessageUtils;
import utils.TimeUtil;

/***
 * 
 * 次日登录脚本
 * 
 * @author lihongji
 *
 */
public class TomorrowSignManagerScript extends ISignScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TOMORROW_SIGN_SCRIPT.Value();
    }

    @Override
    public void addRecord(Player player, int param) {
        TomorrowSignRecord tsrd = player.getTomorrowSignManager().getTsrd();
        tsrd.setType(TomorrowSignRecord.HAVA_GET);
        tsrd.setEndTime(TimeUtil.getNextZeroClock());
    }

    @Override
    public boolean checkAchieve(Player player) {
        TomorrowSignRecord tsrd = player.getTomorrowSignManager().getTsrd();
        if (tsrd.getType() == TomorrowSignRecord.CAN_GET)
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder getAward(Player player) {
        ResultSubmitActivity.Builder builder = ResultSubmitActivity.newBuilder();
        // 检测是否可以领取奖励
        if (!checkAchieve(player))
            MessageUtils.throwCondtionError(GameErrorCode.TODAY_SIGNED);
        // 封装数据
        builder.setActivityid(TomorrowSignManager.TOMORROWSIGN_ID);
        List<RewardsMsg> rewardsMsgs = Lists.newArrayList();
        BagManager bm = player.getBagManager();
        TomorrowSignCfgBean tomorrowSign = (TomorrowSignCfgBean) getGift().get(0);
        Map<Integer, Integer> giftMap = tomorrowSign.getGifts();
        for (Map.Entry<Integer, Integer> pathEntry : giftMap.entrySet()) {
            rewardsMsgs.add(RewardsMsg.newBuilder().setId(pathEntry.getKey())
                    .setNum(pathEntry.getValue()).build());
        }
        // 增加奖励
        bm.addItems(giftMap, true, EReason.SEVEN_SIGN);
        builder.addAllRewards(rewardsMsgs);
        // 重置记录
        addRecord(player, 0);
        return builder;
    }

    @Override
    public org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder getSignInfo(Player player) {
        TomorrowSignRecord record = player.getTomorrowSignManager().getTsrd();
        ActivityInfoMsg.Builder builder = ActivityInfoMsg.newBuilder();
        TomorrowSignCfgBean tomorrowSign = (TomorrowSignCfgBean) getGift().get(0);
        builder.setCt(ChangeType.DEFAULT);
        builder.setId(TomorrowSignManager.TOMORROWSIGN_ID);
        builder.setStartTime((int) (System.currentTimeMillis() / TimeUtil.SECOND));
        builder.setEndTime(record.getEndTime() != 0 ? (int) (record.getEndTime() / TimeUtil.SECOND)
                : (int) ((System.currentTimeMillis() + TimeUtil.ONE_DAY) / TimeUtil.SECOND));
        builder.setActivityType(TomorrowSignManager.TOMORROWSIGN_ID);
        builder.setResetType(0);
        com.alibaba.fastjson.JSONObject json = new JSONObject();
        json.put("title", GameDataManager.getStringCfgBean(Integer.parseInt(tomorrowSign.getName()))
                .getText());
        json.put("bg", tomorrowSign.getAdIcon());
        builder.setRemark(json.toJSONString());
        builder.setRank(0);
        // builder.addEntrys(packageSignInfo(1, tomorrowSign, 1));
        ActivityEntryInfoMsg.Builder entryBuilder = ActivityEntryInfoMsg.newBuilder();
        entryBuilder.setId(TomorrowSignManager.TOMORROWSIGN_ID);
        entryBuilder.setNowProgress(1);
        entryBuilder.setMaxProgress(1);
        entryBuilder.setStatus(checkAchieve(player) == true ? 1 : 0);
        com.alibaba.fastjson.JSONObject info = new JSONObject();
        info.put("title", "登陆");
        entryBuilder.setRemark(info.toJSONString());
        entryBuilder.setReward(JSON.toJSONString(tomorrowSign.getGifts()));
        entryBuilder.setRank(1);
        builder.addEntrys(entryBuilder);
        return builder;
    }


    @Override
    public List getGift() {
        return GameDataManager.getTomorrowSignCfgBeans();
    }

    @Override
    public int getSignRecord(Player player) {
        return 0;
    }

    @Override
    public int getAwardStatus(Player player, int index) {
        return 0;
    }

    @Override
    public void acrossDay(EAcrossDayType type, Player player) {
        // 0点跨天
        if (type == EAcrossDayType.SYS_ACROSS_DAY) {
            if (ableSerialize(player) && player.getTomorrowSignManager().getTsrd().getType() == 0) {
                player.getTomorrowSignManager().getTsrd().setType(TomorrowSignRecord.CAN_GET);
                RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
                respBuilder.addActivitys(getSignInfo(player));
                MessageUtils.send(player, respBuilder);
            }
        }
    }

    @Override
    public boolean ableSerialize(Player player) {
        TomorrowSignRecord tsrd = player.getTomorrowSignManager().getTsrd();
        if (tsrd.getEndTime() != 0 && System.currentTimeMillis() > tsrd.getEndTime())
            return false;
        return true;
    }

    @Override
    public void createRoleInitialize(Player player) {

    }

}
