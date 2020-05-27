package logic.endless;

import java.util.Date;

import logic.character.bean.Player;
import logic.endless.bean.PassStageTO;
import script.IScript;

public interface IEndlessCloisterScript extends IScript {
    public int getClientStep(EndlessCloisterManager ecm);

    /** 获取 进入下一阶段的时间(秒) */
    public int getNextStepTime(EndlessCloisterManager ecm);

    public void tick(Date now, EndlessCloisterManager ecm);

    /**
     * 检查活动进行阶段变化
     */
    public void checkStepUpdate(Date date, EndlessCloisterManager ecm);

    /**
     * 检查发放奖励
     */
    public void checkReward(Player player, Date date);

    /** 获取初始挑战层级关卡id */
    public int getInitStageId(Date date, EndlessCloisterManager ecm);

    /** 获取跳过层级 */
    public int getNextStageId(int nowStageId, int costTime, EndlessCloisterManager ecm);

    /** 获取跳过层级 */
    public int getSkipLevel(int costTime, EndlessCloisterManager ecm);

    /** 通知客户端更新活动阶段信息 */
    public void updateStepToClients(EndlessCloisterManager ecm);
    
    /** 通关并获取下关 */
    public int passStageAndGetNext(Player player, PassStageTO passTo);
    
    /** 获取开始关卡 */
    public int getStartStage(Player player);
    
    /** 数据加载初始化 */
    public void createPlayerInitialize(EndlessCloisterManager ecm);

}
