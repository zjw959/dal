package javascript.logic.sign;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg.Builder;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import cn.hutool.core.date.DateUtil;
import data.GameDataManager;
import data.bean.SignCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.sign.ISignScript;
import logic.sign.MonthSignManager;
import logic.sign.record.MonthSignRecord;
import logic.support.MessageUtils;

/***
 * 
 * 月签到脚本
 * 
 * @author lihongji
 *
 */
public class MonthSignManagerScript extends ISignScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.MONTH_SIGN_SCRIPT.Value();
    }

    @Override
    public void addRecord(Player player, int param) {
        player.getMonthSignManager().getMsrd().addRecord(param);
    }

    /** 获取本月的奖励 **/
    @Override
    public List<SignCfgBean> getGift() {
        int month = DateUtil.thisMonth() + 1;
        List<SignCfgBean> yearSign = GameDataManager.getSignCfgBeans();
        ArrayList<SignCfgBean> monthSign = new ArrayList<SignCfgBean>();
        for (SignCfgBean sign : yearSign) {
            if (sign.getMonth() == month)
                monthSign.add(sign);
        }
        return monthSign;
    }

    @Override
    public int getSignRecord(Player player) {
        return 0;
    }


    @Override
    public boolean checkAchieve(Player player) {
        return player.getMonthSignManager().getMsrd().getIndex() < getGift().size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultSubmitActivity.Builder getAward(Player player) {
        ResultSubmitActivity.Builder builder = ResultSubmitActivity.newBuilder();
        builder.setActivityid(MonthSignManager.MONTHSIGN_ID);
        // 判断今天是否可以领取奖励
        if (player.getMonthSignManager().getMsrd().getType() != MonthSignRecord.CAN_GET)
            MessageUtils.throwCondtionError(GameErrorCode.TODAY_SIGNED);
        int month = DateUtil.thisMonth() + 1;
        ArrayList<SignCfgBean> monthSign = (ArrayList<SignCfgBean>) getGift();
        if (player.getMonthSignManager().getMsrd().getIndex() > monthSign.size())
            MessageUtils.throwCondtionError(GameErrorCode.SIGN_MAX);
        // 增加月登录记录
        addRecord(player, month);
        Map<Integer, Integer> giftMap =
                monthSign.get((player.getMonthSignManager().getMsrd().getIndex() - 1)).getGifts();
        // 加入背包
        BagManager bm = player.getBagManager();
        bm.addItems(giftMap, true, EReason.MONTH_SIGN);
        List<RewardsMsg> rewardsMsgs = Lists.newArrayList();
        for (Map.Entry<Integer, Integer> pathEntry : giftMap.entrySet()) {
            rewardsMsgs.add(RewardsMsg.newBuilder().setId(pathEntry.getKey())
                    .setNum(pathEntry.getValue()).build());
        }
        return builder.addAllRewards(rewardsMsgs);

    }

    @Override
    public Builder getSignInfo(Player player) {
        ActivityInfoMsg.Builder builder = ActivityInfoMsg.newBuilder();
        builder.setCt(ChangeType.DEFAULT);
        builder.setId(MonthSignManager.MONTHSIGN_ID);
        builder.setStartTime((int) (System.currentTimeMillis() / 1000));
        builder.setEndTime((int) (System.currentTimeMillis() / 1000) + 3600 * 120);
        builder.setActivityType(MonthSignManager.MONTHSIGN_ID);
        builder.setResetType(0);
        com.alibaba.fastjson.JSONObject json = new JSONObject();

        json.put("title", GameDataManager
                .getStringCfgBean(
                        Integer.parseInt(GameDataManager.getSignCfgBeans().get(0).getName()))
                .getText());
        builder.setRemark(json.toJSONString());
        builder.setRank(0);
        ArrayList<SignCfgBean> monthSign = (ArrayList<SignCfgBean>) getGift();
        for (int i = 0; i < monthSign.size(); i++) {
            SignCfgBean sign = monthSign.get(i);
            ActivityEntryInfoMsg.Builder entryBuilder = ActivityEntryInfoMsg.newBuilder();
            entryBuilder.setId(MonthSignManager.MONTHSIGN_ID);
            entryBuilder.setNowProgress(i);
            entryBuilder.setMaxProgress(monthSign.size());
            entryBuilder.setStatus(getAwardStatus(player, i));
            JSONObject info = new JSONObject();
            info.put("title", "第" + i + "天");
            entryBuilder.setRemark(info.toJSONString());
            entryBuilder.setReward(JSON.toJSONString(sign.getGifts()));
            entryBuilder.setRank(i);
            builder.addEntrys(entryBuilder);
        }
        return builder;
    }

    @Override
    public int getAwardStatus(Player player, int index) {
        index++;
        if (player.getMonthSignManager().getMsrd().getType() != MonthSignRecord.CAN_GET) {
            if (index <= player.getMonthSignManager().getMsrd().getIndex())
                return 2;
            else
                return 0;
        } else {
            if (index < player.getMonthSignManager().getMsrd().getIndex())
                return 2;
            else if (index == player.getMonthSignManager().getMsrd().getIndex())
                return 1;
            else
                return 0;
        }

    }

    @Override
    public void acrossDay(EAcrossDayType type, Player player) {
        // 0点跨天
        if (type == EAcrossDayType.SYS_ACROSS_DAY) {
            int month = DateUtil.month(new Date()) + 1;
            if (player.getMonthSignManager().getMsrd().getMonth() != month
                    || checkAchieve(player)) {
                if (player.getMonthSignManager().getMsrd().getMonth() != month) {
                    player.getMonthSignManager().getMsrd().reset();
                }
                if (checkAchieve(player) && player.getMonthSignManager().getMsrd()
                        .getType() == MonthSignRecord.HAVA_GET) {
                    player.getMonthSignManager().getMsrd().addAbleAward(month);
                }
                RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
                respBuilder.addActivitys(getSignInfo(player));
                MessageUtils.send(player, respBuilder);
            }
        }
    }

    @Override
    public boolean ableSerialize(Player player) {
        return false;
    }

    @Override
    public void createRoleInitialize(Player player) {
        int month = DateUtil.month(new Date()) + 1;
        player.getMonthSignManager().getMsrd().addAbleAward(month);
    }

}
