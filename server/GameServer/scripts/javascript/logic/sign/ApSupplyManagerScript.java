package javascript.logic.sign;

import java.util.List;
import java.util.Map;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity.Builder;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import data.GameDataManager;
import data.bean.EnergySupplementCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.sign.ApSupplyContainer;
import logic.sign.ApSupplyManager;
import logic.sign.ISignScript;
import logic.sign.record.ApSupplyRecord;
import logic.support.MessageUtils;
import utils.TimeUtil;

/***
 * 
 * 体力补给脚本
 * 
 * @author lihongji
 *
 */
public class ApSupplyManagerScript extends ISignScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ENERGY_SIGN_SCRIPT.Value();
    }

    @Override
    public void addRecord(Player player, int param) {
        player.getApSupplyManager().getAsrd().setApSupplyTime(System.currentTimeMillis());
    }

    @Override
    public boolean checkAchieve(Player player) {
        ApSupplyRecord asrd = player.getApSupplyManager().getAsrd();
        long[] timeGift = ApSupplyContainer.getInstance().getSendEnergyTime();
        long timeNow = System.currentTimeMillis();
        if (timeNow > timeGift[0] && timeNow < timeGift[1]) {
            if (asrd.getApSupplyTime() < timeGift[0])
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder getAward(Player player) {
        if (!checkAchieve(player)) {
            MessageUtils.throwCondtionError(GameErrorCode.TODAY_SIGNED, "超出领取体力时间");
        }
        ResultSubmitActivity.Builder builder = ResultSubmitActivity.newBuilder();
        builder.setActivityid(ApSupplyManager.APSUPPLY_ID);
        List<RewardsMsg> rewardsMsgs = Lists.newArrayList();
        BagManager bm = player.getBagManager();
        Map<Integer, Integer> award = GameDataManager
                .getEnergySupplementCfgBean(ApSupplyContainer.getInstance().getGiftId()).getGifts();
        for (Map.Entry<Integer, Integer> entry : award.entrySet()) {
            rewardsMsgs.add(
                    RewardsMsg.newBuilder().setId(entry.getKey()).setNum(entry.getValue()).build());
        }
        // 重置记录
        addRecord(player, 0);
        // 增加奖励
        bm.addItems(award, true, EReason.APSUPPLY_SIGN);
        return builder.addAllRewards(rewardsMsgs);
    }

    @Override
    public org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder getSignInfo(Player player) {
        ActivityInfoMsg.Builder builder = ActivityInfoMsg.newBuilder();
        builder.setCt(ChangeType.DEFAULT);
        builder.setId(ApSupplyManager.APSUPPLY_ID);
        builder.setStartTime((int) (System.currentTimeMillis() / TimeUtil.SECOND));
        builder.setEndTime((int) ((System.currentTimeMillis() + TimeUtil.ONE_DAY) / 1000));
        builder.setActivityType(ApSupplyManager.APSUPPLY_ID);
        builder.setResetType(2);
        com.alibaba.fastjson.JSONObject json = new JSONObject();
        String title = "体力补给";
        json.put("tip", "少年,请按时服药,攻略更多精灵吧!");
        builder.setRank(0);
        List<EnergySupplementCfgBean> list = getGift();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0)
                title = GameDataManager.getStringCfgBean(Integer.parseInt(list.get(i).getName()))
                        .getText();
            ActivityEntryInfoMsg.Builder entryBuilder = ActivityEntryInfoMsg.newBuilder();
            entryBuilder.setId(ApSupplyManager.APSUPPLY_ID);
            entryBuilder.setNowProgress(i);
            entryBuilder.setMaxProgress(list.size());
            entryBuilder.setStatus(checkAchieve(player) == true ? 1 : 2);
            com.alibaba.fastjson.JSONObject info = new JSONObject();
            long[] timeGift = ApSupplyContainer.getInstance().getEnergyTime();
            if (i == 0) {
                info.put("title", "上午");
                info.put("entry_trigger_time_type", 1);
                info.put("entry_trigger_begin_time", (int) (timeGift[0] / TimeUtil.SECOND));
                info.put("entry_trigger_en_time", (int) (timeGift[1] / TimeUtil.SECOND));

            } else {
                info.put("title", "下午");
                info.put("entry_trigger_time_type", 1);
                info.put("entry_trigger_begin_time", 64800);
                info.put("entry_trigger_en_time", 72000);
            }
            entryBuilder.setRemark(info.toJSONString());
            entryBuilder.setReward(JSON.toJSONString(list.get(i).getGifts()));
            entryBuilder.setRank(i);
            builder.addEntrys(entryBuilder);
        }
        json.put("title", title);
        builder.setRemark(json.toJSONString());
        return builder;
    }

    @Override
    public List<EnergySupplementCfgBean> getGift() {
        return GameDataManager.getEnergySupplementCfgBeans();
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

    }

    @Override
    public boolean ableSerialize(Player player) {
        return false;
    }

    @Override
    public void createRoleInitialize(Player player) {

    }

}
