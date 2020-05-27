package logic.city;

import java.util.List;

import org.game.protobuf.s2c.S2CNewBuildingMsg;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import logic.bag.BagManager;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.city.build.PrizeClawGame;
import logic.city.build.bean.PrizeClawRecord;
import logic.constant.DiscreteDataID;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.support.MessageUtils;
import utils.ToolMap;

/***
 * 
 * 抓娃娃游戏
 * 
 * @author lihongji
 *
 */
public class PrizeClawGameManager extends PlayerBaseFunctionManager implements IRoleJsonConverter {

    PrizeClawRecord prizeClawRecord = new PrizeClawRecord();

    /** 获取娃娃们的信息 **/
    public PrizeClawRecord getPrizeClawRecordInfo() {
        if (prizeClawRecord.getPoolId() == 0) {
            PrizeClawGame.getInstance().resetGashaponPool(prizeClawRecord);
        }
        return prizeClawRecord;
    }

    /** 获取抓娃娃的结束时间 **/
    public void setPrizeClawGameEndTime() {
        // 检查门票次数
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.GASHAPON);
        int ticket = ItemConstantId.DAILY_GASHAPON_COUNT;
        BagManager bagManager = getPlayer().getBagManager();
        if (!bagManager.enoughByTemplateId(ticket, 1))
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH_CAN_NOT_PLAYER_GAME,
                    "道具不足不能玩游戏");
        player.getBagManager().removeItemByTempId(ticket, 1, true, EReason.PRIZE_CLAW);
        // 检查CD
        long now = System.currentTimeMillis();
        int cd = ToolMap.getInt("time", bean.getData(), 20) * 1000;
        long endTime = now + cd;
        prizeClawRecord.setEndTime(endTime);
    }

    /** 获取抓玩玩的结束时间 **/
    public long getPrizeClawGameEndTime() {
        return prizeClawRecord.getEndTime();
    }

    /** 请求验证结果 **/
    public S2CNewBuildingMsg.RespCheckGashaponResult.Builder checkPrizeClawReslut(
            List<Integer> cids) {
        return PrizeClawGame.getInstance().reqCheckGashaponResult(cids, prizeClawRecord,
                getPlayer());
    }

    /** 刷新 **/
    public void refreshPrizeClawGame() {
        PrizeClawGame.getInstance().reqRefreshGashaponPool(prizeClawRecord);
    }

}
