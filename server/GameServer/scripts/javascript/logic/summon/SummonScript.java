package javascript.logic.summon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EEventType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.constant.SummonConstant;
import logic.summon.ComposePoint;
import logic.summon.EComposeState;
import logic.summon.EHistoryType;
import logic.summon.HistoryRecord;
import logic.summon.ISummonScript;
import logic.summon.SummonManager;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SSummonMsg;
import org.game.protobuf.c2s.C2SSummonMsg.ComposeFinish;
import org.game.protobuf.c2s.C2SSummonMsg.ComposeSummon;
import org.game.protobuf.c2s.C2SSummonMsg.GetComposeInfo;
import org.game.protobuf.c2s.C2SSummonMsg.ReqHistoryRecord;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import org.game.protobuf.s2c.S2CSummonMsg;
import org.game.protobuf.s2c.S2CSummonMsg.ComposeInfo;
import org.game.protobuf.s2c.S2CSummonMsg.NoticeComposeFinish;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.SummonCfgBean;
import data.bean.SummonComposeCfgBean;
import data.bean.SummonPoolCfgBean;

public class SummonScript extends ISummonScript {
    private static final Logger LOGGER = Logger.getLogger(SummonScript.class);
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.SUMMON_SCRIPT.Value();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void reqSummon(Player player, C2SSummonMsg.Summon msg) {
        // 检查等级
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(5002);
        if (discreteDataCfgBean != null) {
            Map data = discreteDataCfgBean.getData();
            if (data != null) {
                Object obj = data.get("summonPlayerLevel");
                if (obj != null) {
                    int levelLimit = (int) obj;
                    if (player.getLevel() < levelLimit) {
                        // 请求召唤，等级限制
                        MessageUtils.throwCondtionError(GameErrorCode.CAN_NOT_UNLOCK_PLAYER_LEVEL,
                                "召唤：玩家等级不足");
                        return;
                    }
                }
            }
        }

        int cid = msg.getCid();
        int costIndex = msg.getCost();

        SummonCfgBean summonCfgBean = GameDataManager.getSummonCfgBean(cid);
        if (summonCfgBean == null) {
            // 请求召唤，配置表不存在召唤id
            MessageUtils.throwConfigError(GameErrorCode.CLIENT_PARAM_IS_ERR, "召唤id错误,id=" + cid);
            return;
        }

        List costConfig = summonCfgBean.getCost();
        if (costIndex >= costConfig.size()) {
            // 请求召唤，召唤消耗类型错误
            MessageUtils.throwConfigError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "召唤消耗类型错误,costIndex=" + costIndex + ",实际长度=" + costConfig.size());
            return;
        }

        Map<Integer, Integer> cost = (Map<Integer, Integer>) costConfig.get(costIndex);
        if (cost == null) {
            // 请求召唤，召唤消耗等于null
            MessageUtils.throwConfigError(GameErrorCode.CLIENT_PARAM_IS_ERR, "召唤消耗类型错误,cost等于null");
            return;
        }

        BagManager bagManager = player.getBagManager();
        // 需提前判断道具是否足够，不然扣了道具后，后面出现错误
        boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(cost, true, EReason.SUMMON);
        if (!isEnough) {
            // 召唤所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "消耗资源不足");
            return;
        }

        int weightType = getWeightType(player, summonCfgBean);
        List<RewardsMsg> rewards = new ArrayList<>();
        Map<Integer, Integer> itemMap = new HashMap<>();
        for (int i = 0; i < summonCfgBean.getCardCount(); i++) {
            generateGeneralItem(player, summonCfgBean, weightType, itemMap, rewards);
        }
        bagManager.addItems(itemMap, true, EReason.SUMMON);

        SummonManager summonManager = player.getSummonManager();
        Map<Integer, HistoryRecord> historyRecordKV = summonManager.getHistoryRecord();
        EHistoryType historyType = EHistoryType.SUMMON_DIAMOND;
        if (summonCfgBean.getSummonType() == 1) {
            historyType = EHistoryType.SUMMON_DIAMOND;
        } else if (summonCfgBean.getSummonType() == 4) {
            historyType = EHistoryType.SUMMON_FRIENDSHIP;
        }
        HistoryRecord historyRecord = historyRecordKV.get(historyType.value());
        if (historyRecord == null) {
            historyRecord = new HistoryRecord();
            historyRecord.setType(historyType.value());
            historyRecordKV.put(historyType.value(), historyRecord);
        }

        for (Map.Entry<Integer, Integer> entry : itemMap.entrySet()) {
            historyRecord.addRecord(entry.getKey(), entry.getValue());
        }

        S2CSummonMsg.Summon.Builder summonBuilder = S2CSummonMsg.Summon.newBuilder();
        summonBuilder.addAllItem(rewards);
        
        StringBuilder rewardsStr = new StringBuilder();
        rewardsStr.append(player.getPlayerId() + "召唤" + summonCfgBean.getSummonType() + "得到的物品:");
        for(RewardsMsg reward : rewards) {
            rewardsStr.append(reward.getId());
            rewardsStr.append(":");
            rewardsStr.append(reward.getNum());
            rewardsStr.append(",");
        }
        LOGGER.info(rewardsStr.toString());
        // 事件激活
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.SUMMON);
        in.put(EventConditionKey.CARD_COUNT, summonCfgBean.getCardCount());
        player._fireEvent(in, EEventType.OTHER_EVENT.value());
        MessageUtils.send(player, summonBuilder);
        
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createSummonLog(player, EReason.SUMMON.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    /**
     * 生成普通道具
     * 
     * @param summonCfgBean 召唤配置
     * @param weightType 权重index
     * @param rareItemNum 稀有道具数量
     * @return
     */
    @SuppressWarnings("unchecked")
    private void generateGeneralItem(Player player, SummonCfgBean summonCfgBean, int weightType,
            Map<Integer, Integer> itemMap, List<RewardsMsg> rewards) {
        int sumWeight = 0;
        int randomWeight = 0;
        int weight = 0;
        SummonManager summonManager = player.getSummonManager();
        int poolType = summonCfgBean.getPoolType();
        List<SummonPoolCfgBean> specifySummonPoolBeans = new ArrayList<>();
        List<SummonPoolCfgBean> summonPoolCfgBeans = GameDataManager.getSummonPoolCfgBeans();
        boolean isSpecial = false;
        boolean isRare = false;
        if(summonCfgBean.getSummonType() == 1) {
            if (summonManager.getCounter() >= summonCfgBean.getRareItemTimes()) {
                isSpecial = true;
                if (summonManager.getCounter() == 10) {
                    isRare = true;
                }    
            }
        } else {
            if (summonManager.getCounterFD() >= summonCfgBean.getRareItemTimes()) {
                isSpecial = true;
                if (summonManager.getCounterFD() == 10) {
                    isRare = true;
                }    
            }
        }
        for (SummonPoolCfgBean summonPoolCfgBean : summonPoolCfgBeans) {
            if (summonPoolCfgBean.getPoolType() == poolType) {
                if (!isSpecial) {
                    if (summonPoolCfgBean.getQuality() >= summonCfgBean.getMinQuality()[0]
                            && summonPoolCfgBean.getQuality() <= summonCfgBean.getMinQuality()[1]) {
                        continue;
                    }
                }

                if (isRare) {
                    if (summonPoolCfgBean.getQuality() < summonCfgBean.getMinQuality()[0]
                            || summonPoolCfgBean.getQuality() > summonCfgBean.getMinQuality()[1]) {
                        continue;
                    }
                }

                int num = 0;
                Map<Integer, Integer> itemMapTemp = summonPoolCfgBean.getItemMap();
                for (Map.Entry<Integer, Integer> entry : itemMapTemp.entrySet()) {
                    if (itemMap.containsKey(entry.getKey())) {
                        num = itemMap.get(entry.getKey());
                    }
                }
                if (num >= summonPoolCfgBean.getRepeatNum()) {
                    continue;
                }
                specifySummonPoolBeans.add(summonPoolCfgBean);
                sumWeight = sumWeight + summonPoolCfgBean.getWeight()[weightType];
            }
        }

        randomWeight = RandomUtil.randomInt(sumWeight);
        for (SummonPoolCfgBean actualPool : specifySummonPoolBeans) {
            weight = weight + actualPool.getWeight()[weightType];
            if (weight > randomWeight) {
                Map<Integer, Integer> itemMapTemp = actualPool.getItemMap();
                if (actualPool.getQuality() >= summonCfgBean.getMinQuality()[0]
                        && actualPool.getQuality() <= summonCfgBean.getMinQuality()[1]) {
                    if(summonCfgBean.getSummonType() == 1) {
                        summonManager.setCounter(1);
                    } else {
                        summonManager.setCounterFD(1);
                    }
                } else {
                    if(summonCfgBean.getSummonType() == 1) {
                        summonManager.setCounter(summonManager.getCounter() + 1);
                    } else {
                        summonManager.setCounterFD(summonManager.getCounterFD() + 1);
                    }
                }
                RewardsMsg.Builder rewardsMsgBuilder = RewardsMsg.newBuilder();
                for (Map.Entry<Integer, Integer> entry : itemMapTemp.entrySet()) {
                    rewardsMsgBuilder.setId(entry.getKey());
                    rewardsMsgBuilder.setNum(entry.getValue());
                    rewards.add(rewardsMsgBuilder.build());
                }
                mergeItemMap(itemMap, itemMapTemp);
                break;
            }
        }
    }

    private void mergeItemMap(Map<Integer, Integer> map1, Map<Integer, Integer> map2) {
        for (Map.Entry<Integer, Integer> temp : map2.entrySet()) {
            int key = temp.getKey();
            int value = temp.getValue();
            if (map1.containsKey(key)) {
                int num = map1.get(key) + value;
                map1.put(key, num);
            } else {
                map1.put(key, value);
            }
        }
    }

    private int getWeightType(Player player, SummonCfgBean summonCfgBean) {
        // 1.判断是否新手期
        int newPlayerTime = summonCfgBean.getNoobTime();
        long day = DateUtil.betweenDay(new Date(player.getCreateTime()), new Date(), false);
        if (day > newPlayerTime) {
            // 正常
            if (summonCfgBean.getCardCount() == 1) {
                return SummonConstant.WEIGHT_INDEX_OLD_PLAYER_SINGLE;
            } else {
                return SummonConstant.WEIGHT_INDEX_OLD_PLAYER_TEN_TIMES;
            }
        } else {
            // 新手期
            if (summonCfgBean.getCardCount() == 1) {
                return SummonConstant.WEIGHT_INDEX_NEW_PLAYER_SINGLE;
            } else {
                return SummonConstant.WEIGHT_INDEX_NEW_PLAYER_TEN_TIMES;
            }
        }
    }

    @Override
    protected void reqGetComposeInfo(Player player, Map<Integer, ComposePoint> composePointMap,
            GetComposeInfo msg) {
        S2CSummonMsg.GetComposeInfo.Builder composeInfosBuilder =
                S2CSummonMsg.GetComposeInfo.newBuilder();
        for (Map.Entry<Integer, ComposePoint> entry : composePointMap.entrySet()) {
            ComposePoint composePoint = entry.getValue();
            EComposeState state = composePoint.getState();
            if (!state.equals(EComposeState.IDLE)) {
                ComposeInfo.Builder composeInfoBuilder = ComposeInfo.newBuilder();
                composeInfoBuilder.setCid(composePoint.getType());
                int finishTime = (int) (composePoint.getFinishTime() / 1000);
                composeInfoBuilder.setFinishTime(finishTime);
                composeInfosBuilder.addComposeInfos(composeInfoBuilder);
            }
        }
        composeInfosBuilder.setScore(player.getSummonManager().getScore());
        MessageUtils.send(player, composeInfosBuilder);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void reqComposeSummon(Player player, Map<Integer, ComposePoint> composePointMap,
            ComposeSummon msg) {
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(5002);
        if (discreteDataCfgBean != null) {
            Map data = discreteDataCfgBean.getData();
            if (data != null) {
                Object obj = data.get("summonPlayerLevel");
                if (obj != null) {
                    int levelLimit = (int) obj;
                    if (player.getLevel() < levelLimit) {
                        MessageUtils.throwCondtionError(GameErrorCode.CAN_NOT_UNLOCK_PLAYER_LEVEL,
                                "召唤：玩家等级不足");
                        return;
                    }
                }
            }
        }

        int cid = msg.getCid();
        SummonComposeCfgBean summonComposeCfgBean = GameDataManager.getSummonComposeCfgBean(cid);
        if(summonComposeCfgBean == null) {
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "召唤合成配置" + cid + "不存在!");
            return;
        }
        
        ComposePoint composePoint = composePointMap.get(summonComposeCfgBean.getZPointType());
        if (composePoint == null) {
            composePoint = new ComposePoint(summonComposeCfgBean.getZPointType());
            composePointMap.put(composePoint.getId(), composePoint);
        }

        EComposeState composeState = composePoint.getState();
        if (composeState != EComposeState.IDLE) {
            MessageUtils.throwCondtionError(GameErrorCode.COMPOSE_ING, "该质点正在合成");
            return;
        }

        BagManager bagManager = player.getBagManager();
        Map<Integer, Integer> costMap = summonComposeCfgBean.getCost();
        boolean isEnough =
                bagManager.removeItemsByTemplateIdWithCheck(costMap, true, EReason.COMPOSE_SUMMON);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.COMPOSE_COST_IS_NOT_ENOUGH, "消耗资源不足");
            return;
        }

        composePoint.setState(EComposeState.COMPOSING);
        composePoint.setType(cid);
        composePoint
                .setFinishTime(System.currentTimeMillis() + summonComposeCfgBean.getTime() * 1000);

        S2CSummonMsg.ComposeSummon.Builder composeSummonBuilder =
                S2CSummonMsg.ComposeSummon.newBuilder();
        ComposeInfo.Builder composeInfoBuilder = ComposeInfo.newBuilder();
        composeInfoBuilder.setCid(cid);
        int finishTime = (int) (composePoint.getFinishTime() / 1000);
        composeInfoBuilder.setFinishTime(finishTime);
        composeSummonBuilder.setComposeInfo(composeInfoBuilder);
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.COMPOUND);
        player._fireEvent(in, EEventType.OTHER_EVENT.value());
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createSummonLog(player, EReason.COMPOSE_SUMMON.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        MessageUtils.send(player, composeSummonBuilder);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void reqComposeFinish(Player player, Map<Integer, ComposePoint> composePointMap,
            ComposeFinish msg) {
        SummonManager summonManager = player.getSummonManager();
        int id = msg.getZPointType();
        ComposePoint composePoint = composePointMap.get(id);
        if (composePoint == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "没有找到合成对象");
            return;
        }
        if (composePoint.getState() != EComposeState.COMPOSE_FINISH) {
            MessageUtils.throwCondtionError(GameErrorCode.COMPOSE_IS_NOT_FINISH, "合成未完成");
            return;
        }
        int cid = composePoint.getType();
        SummonComposeCfgBean summonComposeCfgBean = GameDataManager.getSummonComposeCfgBean(cid);
        int poolType = 0;
        double rate = RandomUtil.randomInt(10000) / 10000d;
        List<SummonPoolCfgBean> poolCfgBeans = new ArrayList<>();
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(14002);
        Map data = discreteDataCfgBean.getData();
        int baseNum = (int) data.get("rareProbability");
        double rareRate =
                (0.004 * Math.pow(summonManager.getScore() / 10000d, 2.5)) / 100 + baseNum / 10000d;
        if (rareRate > 1) {
            rareRate = 1;
        }
        if (rate < rareRate) {
            poolType = summonComposeCfgBean.getRarePoolType();
        } else {
            poolType = summonComposeCfgBean.getPoolType();
        }
        List<SummonPoolCfgBean> summonPoolCfgBeans = GameDataManager.getSummonPoolCfgBeans();
        for (SummonPoolCfgBean summonPoolCfgBean : summonPoolCfgBeans) {
            if (summonPoolCfgBean.getPoolType() == poolType) {
                poolCfgBeans.add(summonPoolCfgBean);
            }
        }
        int sumWeight = 0;
        for (SummonPoolCfgBean bean : poolCfgBeans) {
            sumWeight = sumWeight + bean.getWeight()[SummonConstant.WEIGHT_INDEX_OLD_PLAYER_SINGLE];
        }
        int randomWeight = RandomUtil.randomInt(sumWeight);
        int weightTemp = 0;
        Map<Integer, Integer> itemMap = null;
        for (SummonPoolCfgBean bean : poolCfgBeans) {
            weightTemp =
                    weightTemp + bean.getWeight()[SummonConstant.WEIGHT_INDEX_OLD_PLAYER_SINGLE];
            if (weightTemp > randomWeight) {
                itemMap = bean.getItemMap();
                if (bean.getComposeReset()) {
                    summonManager.setScore(0);
                } else {
                    discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(14003);
                    data = discreteDataCfgBean.getData();
                    int score = (int) data.get(summonComposeCfgBean.getLevel());
                    summonManager.setScore(summonManager.getScore() + score);
                }
                break;
            }
        }
        BagManager bagManager = player.getBagManager();
        bagManager.addItems(itemMap, true, EReason.COMPOSE_SUMMON);

        composePoint.setState(EComposeState.IDLE);
        composePoint.setType(0);
        composePoint.setFinishTime(0);

        Map<Integer, HistoryRecord> historyRecordKV = summonManager.getHistoryRecord();
        HistoryRecord historyRecord = historyRecordKV.get(EHistoryType.SUMMON_COMPOSE.value());
        if (historyRecord == null) {
            historyRecord = new HistoryRecord();
            historyRecord.setType(EHistoryType.SUMMON_COMPOSE.value());
            historyRecordKV.put(historyRecord.getType(), historyRecord);
        }
        for (Map.Entry<Integer, Integer> entry : itemMap.entrySet()) {
            historyRecord.addRecord(entry.getKey(), entry.getValue());
        }

        S2CSummonMsg.ComposeFinish.Builder composeFinishBuilder =
                S2CSummonMsg.ComposeFinish.newBuilder();
        for (Map.Entry<Integer, Integer> entry : itemMap.entrySet()) {
            RewardsMsg.Builder rewardBuilder = RewardsMsg.newBuilder();
            rewardBuilder.setId(entry.getKey());
            rewardBuilder.setNum(entry.getValue());
            composeFinishBuilder.addItem(rewardBuilder);
        }
        composeFinishBuilder.setZPointType(id);
        composeFinishBuilder.setScore(summonManager.getScore());
        MessageUtils.send(player, composeFinishBuilder);
    }

    @Override
    protected void tick(Player player, Map<Integer, ComposePoint> composePointMap) {
        List<Integer> cids = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<Integer, ComposePoint> entry : composePointMap.entrySet()) {
            ComposePoint value = entry.getValue();
            EComposeState state = value.getState();
            if (state == EComposeState.COMPOSING) {
                if (currentTime >= value.getFinishTime()) {
                    value.setState(EComposeState.COMPOSE_FINISH);
                    cids.add(value.getType());
                }
            }
        }

        if (!cids.isEmpty()) {
            NoticeComposeFinish.Builder noticeComposeFinishBuilder =
                    NoticeComposeFinish.newBuilder();
            noticeComposeFinishBuilder.addAllCid(cids);
            MessageUtils.send(player, noticeComposeFinishBuilder);
        }
    }

    @Override
    protected void createRoleInitialize(Map<Integer, ComposePoint> composePointMap) {
        List<SummonComposeCfgBean> summonComposeCfgBeans =
                GameDataManager.getSummonComposeCfgBeans();
        for (SummonComposeCfgBean summonComposeCfgBean : summonComposeCfgBeans) {
            if (!composePointMap.containsKey(summonComposeCfgBean.getZPointType())) {
                ComposePoint composePoint = new ComposePoint(summonComposeCfgBean.getZPointType());
                composePointMap.put(composePoint.getId(), composePoint);
            }
        }
    }

    @Override
    protected void reqHistoryRecord(Player player, Map<Integer, HistoryRecord> historyRecordKV,
            ReqHistoryRecord msg) {
        List<Integer> types = msg.getTypeList();
        S2CSummonMsg.ResHistoryRecord.Builder resHistoryRecord = S2CSummonMsg.ResHistoryRecord.newBuilder();
        for(Integer type : types) {
            HistoryRecord historyRecord = historyRecordKV.get(type);
            if (historyRecord == null) {
                historyRecord = new HistoryRecord();
                historyRecord.setType(EHistoryType.getHistoryType(type).value());
            }
            resHistoryRecord.addHistoryRecords(historyRecord.buildHistoryRecord());
        }
        
        MessageUtils.send(player, resHistoryRecord);
    }

    @Override
    protected void createPlayerInitialize(Map<Integer, ComposePoint> composePointMap) {
        List<SummonComposeCfgBean> summonComposeCfgBeans =
                GameDataManager.getSummonComposeCfgBeans();
        for (SummonComposeCfgBean summonComposeCfgBean : summonComposeCfgBeans) {
            if (!composePointMap.containsKey(summonComposeCfgBean.getZPointType())) {
                ComposePoint composePoint = new ComposePoint(summonComposeCfgBean.getZPointType());
                composePointMap.put(composePoint.getId(), composePoint);
            }
        }
    }
}
