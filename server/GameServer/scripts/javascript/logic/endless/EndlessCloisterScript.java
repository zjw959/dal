package javascript.logic.endless;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EndlessCloisterConstant;
import logic.constant.GameErrorCode;
import logic.endless.EndlessCloisterManager;
import logic.endless.IEndlessCloisterScript;
import logic.endless.bean.EndlessVO;
import logic.endless.bean.PassStageTO;
import logic.item.ItemUtils;
import logic.mail.MailService;
import logic.msgBuilder.EndlessCloisterMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspEndlessCloisterInfo;

import thread.log.LogProcessor;
import utils.DateEx;
import utils.ExceptionEx;
import utils.TimeUtil;

import com.alibaba.fastjson.JSONArray;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.EndlessCloisterLevelCfgBean;

public class EndlessCloisterScript implements IEndlessCloisterScript {
    static final Logger log = Logger.getLogger(EndlessCloisterScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ENDLESS_DUNGEON_SCRIPT.Value();
    }

    @Override
    public int getStartStage(Player player) {
        EndlessCloisterManager ecm = player.getEndlessCloisterManager();
        EndlessVO endlessVO = ecm.getEndlessVO();
        int startStageId = 0;
        if (ecm.getStep() == EndlessCloisterConstant.STEP_OPEN) {// 活动开启阶段
            if (endlessVO.getNowStage() <= 0) {
                startStageId = ecm.getInitStageId(new Date());
                endlessVO.setNowStage(startStageId);
            } else {
                startStageId = endlessVO.getNowStage();
            }
        }
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(player, startStageId, 0, null,
                            EReason.DUNGEON_LOG_ENDLESS_START.value(), null));
        } catch (Exception ex) {
            log.error(ExceptionEx.e2s(ex));
        }
        return startStageId;
    }
    
    @Override
    public int passStageAndGetNext(Player player, PassStageTO passTo) {
        EndlessCloisterManager ecm = player.getEndlessCloisterManager();

        // 计算下一关卡
        int nextStageId = 0;
        EndlessVO endlessVO = ecm.getEndlessVO();
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(player, endlessVO.getNowStage(), 1, null,
                            EReason.DUNGEON_LOG_ENDLESS_OVER.value(), null));
        } catch (Exception ex) {
            log.error(ExceptionEx.e2s(ex));
        }
        if (ecm.getStep() == EndlessCloisterConstant.STEP_OPEN
                || (ecm.getStep() == EndlessCloisterConstant.STEP_BALANCE && !endlessVO
                        .isTimeUpSave())) {// 活动开启或结算阶段
            // 过期保留使用
            if (ecm.getStep() == EndlessCloisterConstant.STEP_BALANCE)
                endlessVO.setTimeUpSave(true);
            int stageId = endlessVO.getNowStage();// 关卡id
            if (passTo.getLevelCid() != stageId)
                MessageUtils.throwCondtionError(GameErrorCode.ENDLESS_STAGE_NOT_MATCH);
            int costTime = passTo.getCostTime();// 耗时（秒）
            endlessVO.setTodayCostTime(endlessVO.getTodayCostTime() + costTime);
            endlessVO.setLastPassTime(System.currentTimeMillis());
            nextStageId = ecm.getNextStageId(stageId, costTime);
            if (nextStageId > stageId ||
            // 通关最高层数
                    (nextStageId == stageId && endlessVO.getTodayBest() < nextStageId)) {
                endlessVO.setNowStage(nextStageId);
                int bestStage = nextStageId - 1;// 今日最高通关关卡id
                if (nextStageId == stageId)
                    bestStage = nextStageId;
                endlessVO.setTodayBest(bestStage);// 因为后续要根据关卡id发奖，所以这里保存的是关卡id
                EndlessCloisterLevelCfgBean bestCfg = GameDataManager.getEndlessCloisterLevelCfgBean(bestStage);
                if (bestCfg == null)
                    MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                            "EndlessCloisterLevelCfgBean [", String.valueOf(bestStage),
                            "] does not exist");
                int best = bestCfg.getOrder();
                if (best > endlessVO.getHistoryBest()) {
                    endlessVO.setHistoryBest(best);
                }
                // 通关奖励
                Map<Integer, Integer> out = new HashMap<Integer, Integer>();
                for (int i = stageId; i <= bestStage; i++) {
                    Map<Integer, Integer> in = new HashMap<Integer, Integer>(1);
                    bestCfg = GameDataManager.getEndlessCloisterLevelCfgBean(stageId);
                    if (bestCfg == null)
                        MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                                "EndlessCloisterLevelCfgBean [", String.valueOf(stageId),
                                "] does not exist");
                    in.put(bestCfg.getReward(), 1);
                    out.putAll(ItemUtils.unpackItems(player, null, in, true,
                                    EReason.DUNGEON_ENDLESS_PASS));
                }
                passTo.setPassRewards(out);
            }
            if (ecm.getStep() == EndlessCloisterConstant.STEP_BALANCE) {
                nextStageId = 0;
            }
        }
        // 通关后直接进入下层挑战，即直接开始无尽关卡
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(player, nextStageId, 0, null,
                            EReason.DUNGEON_LOG_ENDLESS_START.value(), null));
        } catch (Exception ex) {
            log.error(ExceptionEx.e2s(ex));
        }
        return nextStageId;
    }
    
    @Override
    public int getClientStep(EndlessCloisterManager ecm) {
        // 这里没有被manager.getClientStep()调用
        // 对于客户端来说，结束阶段也是准备阶段
        return ecm.getStep() == EndlessCloisterConstant.STEP_OVER ? EndlessCloisterConstant.STEP_READY
                : ecm.getStep();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getNextStepTime(EndlessCloisterManager ecm) {

        long time = 0;
        if (ecm.getStep() == EndlessCloisterConstant.STEP_OPEN) {// 开启阶段
            time = ecm.getBalanceTime();
        } else if (ecm.getStep() == EndlessCloisterConstant.STEP_BALANCE) {// 结算阶段
            time = ecm.getResetTime();
        } else if (ecm.getStep() == EndlessCloisterConstant.STEP_OVER) {// 结束阶段
            // 查找下一个周期开始时间
            DiscreteDataCfgBean dataCfgTemp =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ENDLESS_CLOISTER);
            List<Map<String, List<Integer>>> duration =
                    (List<Map<String, List<Integer>>>) dataCfgTemp.getData().get(
                            DiscreteDataKey.ENDLESS_DURATION);
            Date baseWeekDate = new Date();
            Map<String, List<Integer>> timeInfo =
                    duration.get(ecm.getCurrentIndex() % ecm.getWeekCircle());
            // 活动开启时间
            List<Integer> opentime = timeInfo.get(DiscreteDataKey.OPEN_TIME);
            // 跨大周期进行一周时间的偏移
            if (ecm.getCurrentIndex() >= ecm.getWeekCircle())
                baseWeekDate.setTime(ecm.getOpenTime() + TimeUtil.ONE_WEEK);
            time = getTimestamp(opentime, baseWeekDate);
        }
        return (int) (time / 1000);

    }

    /** 跳级封装对象集合 */
    private List<SkipLevelsObj> parseSkipLevels() {
        DiscreteDataCfgBean dataCfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ENDLESS_CLOISTER);
        JSONArray array = (JSONArray) dataCfg.getData().get(DiscreteDataKey.SKIP_LEVELS);
        List<SkipLevelsObj> list = new ArrayList<SkipLevelsObj>();
        for (int i = 0; i < array.size(); i++) {
            String str = array.getString(i);
            String[] skip = StringUtils.split(str, ":");
            int skipLev = Integer.parseInt(skip[1]);
            String[] time = StringUtils.split(skip[0], "-");
            int minTime = Integer.parseInt(time[0]);
            int maxTime = Integer.parseInt(time[1]);
            SkipLevelsObj obj = new SkipLevelsObj(minTime, maxTime, skipLev);
            list.add(obj);
        }
        return list;
    }

    /** 解析每周期的挑战关卡ID集合 */
    private Map<Integer, List<Integer>> parseWeekLevelMap() {
        Map<Integer, List<Integer>> weekLevelMap = new HashMap<Integer, List<Integer>>();
        List<EndlessCloisterLevelCfgBean> list = GameDataManager.getEndlessCloisterLevelCfgBeans();
        for (EndlessCloisterLevelCfgBean cfg : list) {
            if (cfg.getId() <= 0) {
                continue;
            }
            List<Integer> ll = weekLevelMap.get(cfg.getWeek());
            if (ll == null) {
                ll = new ArrayList<Integer>();
                weekLevelMap.put(cfg.getWeek(), ll);
            }
            ll.add(cfg.getId());
        }
        return weekLevelMap;
    }

    /** 解析各个时间 */
    @SuppressWarnings("unchecked")
    private void parseTimes(Date date, EndlessCloisterManager ecm) {

        DiscreteDataCfgBean dataCfgTemp =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ENDLESS_CLOISTER);
        List<Map<String, List<Integer>>> duration =
                (List<Map<String, List<Integer>>>) dataCfgTemp.getData().get(
                        DiscreteDataKey.ENDLESS_DURATION);
        ecm.setWeekCircle(duration.size());
        int currentIndex = 0;
        for (Map<String, List<Integer>> map : duration) {
            currentIndex++;
            // 活动开启时间
            List<Integer> opentime = map.get(DiscreteDataKey.OPEN_TIME);
            long openTime = getTimestamp(opentime, date);

            // 发放奖励及重置时间
            List<Integer> resettingtime = map.get(DiscreteDataKey.RESETTING_TIME);
            long resetTime = getTimestamp(resettingtime, date);

            // 对当前时间的判定
            if (date.getTime() < openTime)
                break;
            // 活动结算时间
            List<Integer> balancetime = map.get(DiscreteDataKey.BALANCE_TIME);
            ecm.setOpenTime(openTime);
            ecm.setResetTime(resetTime);
            ecm.setBalanceTime(getTimestamp(balancetime, date));
            ecm.setCurrentIndex(currentIndex);
        }
    }

    private long getTimestamp(List<Integer> timeInfo, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // 当前为周日时，策划默认周日为一周的结束，在程序概念上须回退一周
        if(cal.get(Calendar.DAY_OF_WEEK) == 1) {
            cal.add(Calendar.WEEK_OF_YEAR, -1);
        }
        // 第一部分为周天数
        int weekDay = timeInfo.get(0);
        // 从周日=1开始依次类推
        cal.set(Calendar.DAY_OF_WEEK, weekDay == 7 ? 1 : weekDay + 1);
        // 策划配置中周日在程序概念上必定为下周的开始
        if(weekDay == 7) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        // 第一部分为零点至此时分钟数
        int minuPast = timeInfo.get(1);
        int hourPast = minuPast / (int) (TimeUtil.ONE_HOUR / TimeUtil.SECOND);
        int leftMinu = minuPast % (int) (TimeUtil.ONE_HOUR / TimeUtil.SECOND);
        cal.set(Calendar.HOUR_OF_DAY, hourPast);
        cal.set(Calendar.MINUTE, leftMinu);
        return cal.getTimeInMillis();
    }

    @Override
    public void tick(Date now, EndlessCloisterManager ecm) {
        initEndlessCircle(now, ecm);
    }

    @Override
    public void createPlayerInitialize(EndlessCloisterManager ecm) {
        initEndlessCircle(new Date(), ecm);        
    }
    
    private void initEndlessCircle(Date now, EndlessCloisterManager ecm) {
        // 开放时间段变更
        parseTimes(now, ecm);
        checkStepUpdate(now, ecm);
    }

    @Override
    public void checkStepUpdate(Date date, EndlessCloisterManager ecm) {
        // 判定当前周期数与记录周期数是否匹配，跨周期的处理
        long circleNow = getCirclesFromBasePoint(date, ecm);
        EndlessVO endlessVO = ecm.getEndlessVO();
        // 跨周期，处理奖励
        if (circleNow != endlessVO.getVoCircles()) {
            log.debug("----检测无尽回廊跨周期发奖及重置---");
            // 上周期没有发奖进行补偿
            if (EndlessCloisterConstant.STEP_OVER != ecm.getStep()) {
                checkReward(ecm.getPlayer(), date);
                ecm.setStep(EndlessCloisterConstant.STEP_OVER);
            }
            // 重置为当前周期
            endlessVO.setVoCircles(circleNow);
        }
        long t = date.getTime();
        int oldStep = ecm.getStep();
        // 对动态配置修改的适配
        // 开启阶段
        if (t >= ecm.getOpenTime() && ecm.getBalanceTime() > t) {
            if (oldStep != EndlessCloisterConstant.STEP_OPEN) {
                log.debug("----无尽回廊进入开始阶段---");
                ecm.setStep(EndlessCloisterConstant.STEP_OPEN);
            }
        }
        // 结算阶段
        else if (t >= ecm.getBalanceTime() && ecm.getResetTime() > t) {
            if (oldStep != EndlessCloisterConstant.STEP_BALANCE) {
                ecm.setStep(EndlessCloisterConstant.STEP_BALANCE);
                log.debug("----无尽回廊进入结算阶段---");
            }
        }
        // 准备阶段
        else if (t >= ecm.getResetTime()) {
            if (oldStep != EndlessCloisterConstant.STEP_OVER) {
                log.debug("----无尽回廊发奖及重置---");
                // 发放奖励(假设重启服务器后重复调用也没关系，玩家如果今日已经发过奖励，将不再发放)
                // 玩家线程处理
                checkReward(ecm.getPlayer(), date);
                ecm.setStep(EndlessCloisterConstant.STEP_OVER);
            }
        }
        // 通知客户端更新活动阶段信息
        if (oldStep != ecm.getStep()) {
            updateStepToClients(ecm);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void checkReward(Player player, Date date) {
        EndlessCloisterManager ecm = player.getEndlessCloisterManager();
        EndlessVO endlessVO = ecm.getEndlessVO();
        // 跳过没有挑战的
        int todayBest = endlessVO.getTodayBest();
        if (todayBest == 0) {
            return;
        }
        // 跳过今日已领奖的
        if (ecm.getStep() == EndlessCloisterConstant.STEP_OVER) {
            return;
        }
        long nowTime = System.currentTimeMillis();
        // 今日已经过了领奖时间或者上次通关时间不是今日，则补发奖励
        if (nowTime >= ecm.getResetTime()
                || getCirclesFromBasePoint(date, ecm) != endlessVO.getVoCircles()) {
            endlessVO.setLastRewardTime(nowTime);
            // 邮件发奖
            EndlessCloisterLevelCfgBean cfg =
                    GameDataManager.getEndlessCloisterLevelCfgBean(todayBest);
            if (cfg != null) {
                // LogDsp logDsp =
                // LogDsp.newBuilder(GoodsDsp.ENDLESS_CLOISTER_BALANCE).addAttr("todayBest",
                // todayBest);
                // 邮件文本
                String title = GameDataManager.getStringCfgBean(212005).getText();
                String body = GameDataManager.getStringCfgBean(212006).getText();
                // 替换占位符
                body = MessageFormat.format(body, cfg.getOrder());
                MailService.getInstance().sendPlayerMail(false, player.getPlayerId(), title, body,
                        cfg.getFloorReward(), EReason.DUNGEON_ENDLESS_SETTLED);
                // MailManager.me().sendPersonalMail(0, player.getId(),
                // MailConstant.TEXT_TYPE_CONFIG,
                // "mail.endless.title", "mail.endless.content", map, cfg.getFloorReward(),
                // logDsp);
            }
            // 重置
            endlessVO.setTodayBest(0);
            endlessVO.setTodayCostTime(0);
            endlessVO.setNowStage(0);
            endlessVO.setTimeUpSave(false);
        }
    }

    @Override
    public int getInitStageId(Date date, EndlessCloisterManager ecm) {
        Map<Integer, List<Integer>> weekLevelMap = parseWeekLevelMap();
        // 周期偏移量
        long circle = getCirclesFromBasePoint(date, ecm);
        // 余数都是从0开始的，不会等于除数自身，因此做加1操作
        int offsetIndex = (int) ((circle) % weekLevelMap.size()) + 1;
        // 同配置类型数量进行比较,这里暂时信任连续类型
        List<Integer> list = weekLevelMap.get(offsetIndex);
        if(list == null || list.size() <= 0)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                    "mode week day [", String.valueOf(offsetIndex), "] config is null");
        return list.get(0);
    }

    private long getCirclesFromBasePoint(Date date, EndlessCloisterManager ecm) {
        // 时间基准点选取为0-UTC
        long baseTime = 0;
        // 以周为周期进行推演,当前参考时间为下周1
        Date nextWeek = DateEx.getNextWeekHour(date, 1, 0);
        // 此时经过的周期数,配置数组长度次/周
        long elderCircle = ecm.getWeekCircle() * ((nextWeek.getTime() - baseTime) / TimeUtil.ONE_WEEK);
        return elderCircle + ecm.getCurrentIndex();
    }

    @Override
    public int getNextStageId(int nowStageId, int costTime, EndlessCloisterManager ecm) {
        DiscreteDataCfgBean dataCfgTemp =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ENDLESS_CLOISTER_MAX);
        int maxLv = (Integer) dataCfgTemp.getData().get(DiscreteDataKey.ENDLESS_MAX_LV);
        int nextStageId = nowStageId + 1;// 默认逐级挑战
        EndlessCloisterLevelCfgBean cfg =
                GameDataManager.getEndlessCloisterLevelCfgBean(nextStageId);;
        EndlessCloisterLevelCfgBean nowCfg =
                GameDataManager.getEndlessCloisterLevelCfgBean(nowStageId);
        // 层数限制
        if (cfg == null || cfg.getOrder() > maxLv || nowCfg.getWeek() != cfg.getWeek()) {
            return nowStageId;// 到达满级（配置表里找不到了）
        }
        int skipLev = getSkipLevel(costTime, ecm);
        // 最大层数限制
        if (cfg.getOrder() + skipLev > maxLv)
            skipLev = maxLv - cfg.getOrder();
        if (skipLev > 0) {
            // 计算中间有无boss关卡，有boss关卡先跳到boss关卡
            int k = 0;
            for (int i = 0; i < skipLev; i++) {
                cfg = GameDataManager.getEndlessCloisterLevelCfgBean(nextStageId + i);
                if (cfg == null || cfg.getDifficulty() == 2) {
                    break;
                }
                if (nowCfg.getWeek() != cfg.getWeek()) {
                    break;// 到达满级（配置表里找不到了）
                }
                k++;
            }
            nextStageId += k;
        }
        return nextStageId;
    }

    @Override
    public int getSkipLevel(int costTime, EndlessCloisterManager ecm) {
        List<SkipLevelsObj> skipList = parseSkipLevels();
        for (SkipLevelsObj obj : skipList) {
            if (costTime >= obj.getMinTime() && costTime <= obj.getMaxTime()) {
                return obj.getSkipLev();
            }
        }
        return 0;
    }

    @Override
    public void updateStepToClients(EndlessCloisterManager ecm) {
        // 玩家线程处理
        RspEndlessCloisterInfo.Builder info =
                EndlessCloisterMsgBuilder.createEndlessCloisterInfo(ecm.getPlayer());
        MessageUtils.send(ecm.getPlayer(), info);
    }

    /** 跳级封装对象 */
    private class SkipLevelsObj {
        /** 最小时间 */
        int minTime;
        /** 最大时间 */
        int maxTime;
        /** 跳过层级 */
        int skipLev;

        public SkipLevelsObj(int minTime, int maxTime, int skipLev) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.skipLev = skipLev;
        }

        public int getMinTime() {
            return minTime;
        }

        public int getMaxTime() {
            return maxTime;
        }

        public int getSkipLev() {
            return skipLev;
        }
    }
}
