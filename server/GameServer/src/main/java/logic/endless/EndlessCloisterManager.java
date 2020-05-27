package logic.endless;

import java.util.Date;

import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.EndlessCloisterConstant;
import logic.endless.bean.EndlessVO;
import logic.endless.bean.PassStageTO;
import logic.support.LogicScriptsUtils;


/**
 * 无尽回廊管理器
 * 
 * @author liu.jiang
 * @date 2018年3月13日
 */
public class EndlessCloisterManager extends PlayerBaseFunctionManager implements
        IRoleJsonConverter, ITick, ICreatePlayerInitialize {


    /** 活动阶段 */
    private int step;
    /** 活动开启时间 */
    transient private long openTime;
    /** 活动结算时间 */
    transient private long balanceTime;
    /** 发放奖励及重置时间 */
    transient private long resetTime;
    /** 当前周区间索引 */
    transient private int currentIndex;
    /** 周区间数 */
    transient private int weekCircle;

    EndlessVO endlessVO = new EndlessVO();

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getClientStep() {
        // 对于客户端来说，结束阶段也是准备阶段
        return step == EndlessCloisterConstant.STEP_OVER ? EndlessCloisterConstant.STEP_READY
                : step;
    }

    /** 获取 进入下一阶段的时间(秒) */
    public int getNextStepTime() {
        return LogicScriptsUtils.getIEndlessCloisterScript().getNextStepTime(this);
    }

    @Override
    public void tick() {
        // 开放时间段变更
        Date now = new Date();
        LogicScriptsUtils.getIEndlessCloisterScript().tick(now, this);
    }

    /**
     * 检查活动进行阶段变化
     */
    public void checkStepUpdate(Date date) {
        LogicScriptsUtils.getIEndlessCloisterScript().checkStepUpdate(date, this);
    }

    /**
     * 检查发放奖励
     */
    public void checkReward(Player player, Date date) {
        LogicScriptsUtils.getIEndlessCloisterScript().checkReward(player, date);
    }

    /** 获取初始挑战层级关卡id */
    public int getInitStageId(Date date) {
        return LogicScriptsUtils.getIEndlessCloisterScript().getInitStageId(date, this);
    }

    /** 获取跳过层级 */
    public int getNextStageId(int nowStageId, int costTime) {
        return LogicScriptsUtils.getIEndlessCloisterScript().getNextStageId(nowStageId, costTime,
                this);
    }

    /** 获取跳过层级 */
    public int getSkipLevel(int costTime) {
        return LogicScriptsUtils.getIEndlessCloisterScript().getSkipLevel(costTime, this);
    }

    /** 通知客户端更新活动阶段信息 */
    public void updateStepToClients() {
        LogicScriptsUtils.getIEndlessCloisterScript().updateStepToClients(this);
    }

    /** 通关并获取下关 */
    public int passStageAndGetNext(PassStageTO passTo) {
        return LogicScriptsUtils.getIEndlessCloisterScript().passStageAndGetNext(player, passTo);
    }
    
    /** 获取开始关卡 */
    public int getStartStage() {
        return LogicScriptsUtils.getIEndlessCloisterScript().getStartStage(player);
    }

    @Override
    public void createPlayerInitialize() {
        LogicScriptsUtils.getIEndlessCloisterScript().createPlayerInitialize(this);
    }
    
    public EndlessVO getEndlessVO() {
        return endlessVO;
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public long getBalanceTime() {
        return balanceTime;
    }

    public void setBalanceTime(long balanceTime) {
        this.balanceTime = balanceTime;
    }

    public long getResetTime() {
        return resetTime;
    }

    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getWeekCircle() {
        return weekCircle;
    }

    public void setWeekCircle(int weekCircle) {
        this.weekCircle = weekCircle;
    }

    public Player getPlayer() {
        return player;
    }

}
