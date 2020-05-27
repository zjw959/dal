package logic.msgBuilder;

import java.util.List;

import logic.character.bean.Player;
import logic.endless.EndlessCloisterManager;
import logic.endless.bean.EndlessVO;

import org.game.protobuf.s2c.S2CEndlessCloisterMsg.EndlessCloisterInfo;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspEndlessCloisterInfo;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspPassStageEndless;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspStartFightEndless;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import data.GameDataManager;
import data.bean.EndlessCloisterLevelCfgBean;

/**
 * 无尽回廊消息构建器
 * 
 * @author liu.jiang
 * @date 2018年3月14日
 */
public class EndlessCloisterMsgBuilder {

    /** 创建无尽回廊信息 */
    public static RspEndlessCloisterInfo.Builder createEndlessCloisterInfo(Player player) {
        RspEndlessCloisterInfo.Builder builder = RspEndlessCloisterInfo.newBuilder();
        builder.setInfo(createBaseInfo(player.getEndlessCloisterManager()));
        return builder;
    }

    /** 创建无尽回廊开始战斗响应信息 */
    public static RspStartFightEndless.Builder createRspStartFightEndless(EndlessVO endlessVO,
            int startLevelCid) {
        RspStartFightEndless.Builder builder = RspStartFightEndless.newBuilder();
        builder.setLevelCid(startLevelCid);
        return builder;
    }

    /** 创建无尽回廊通关响应信息 */
    public static RspPassStageEndless.Builder createRspPassStageEndless(EndlessVO endlessVO,
            List<RewardsMsg> rewards, int nextLevelCid) {
        RspPassStageEndless.Builder builder = RspPassStageEndless.newBuilder();
        builder.setNextLevelCid(nextLevelCid);
        builder.addAllRewards(rewards);
        return builder;
    }

    /** 创建无尽回廊基础信息 */
    public static EndlessCloisterInfo.Builder createBaseInfo(EndlessCloisterManager ecm) {
        EndlessVO endlessVO = ecm.getEndlessVO();
        EndlessCloisterInfo.Builder info = EndlessCloisterInfo.newBuilder();
        info.setHistoryBest(endlessVO.getHistoryBest());
        EndlessCloisterLevelCfgBean cfg =
                GameDataManager.getEndlessCloisterLevelCfgBean(endlessVO.getTodayBest());
        int todayBest = cfg == null ? 0 : cfg.getOrder();
        info.setTodayBest(todayBest);
        info.setTodayCostTime(endlessVO.getTodayCostTime());
        info.setStep(ecm.getClientStep());
        info.setNextStepTime(ecm.getNextStepTime());
        return info;
    }

}
