/**
 * 
 */
package logic.summon;

import java.util.HashMap;
import java.util.Map;

import org.game.protobuf.c2s.C2SSummonMsg;

import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.support.LogicScriptsUtils;

public class SummonManager extends PlayerBaseFunctionManager
        implements ICreatePlayerInitialize, IRoleJsonConverter, ITick {

    /** 计数器 */
    private int counter = 1;
    /** 友情召唤计数器 */
    private int counterFD = 1;
    /** 祈願稀有產出概率 */
    private int score;
    /** 祈愿召唤 */
    private Map<Integer, ComposePoint> composePointMap = new HashMap<>();
    /** 抽獎記錄 */
    private Map<Integer, HistoryRecord> historyRecordKV = new HashMap<>();

    @Override
    public void createPlayerInitialize() {
        LogicScriptsUtils.getISummonScript().createPlayerInitialize(composePointMap);
    }
    
    /**
     * 召唤
     * 
     * @param msg
     */
    public void reqSummon(C2SSummonMsg.Summon msg) {
        LogicScriptsUtils.getISummonScript().reqSummon(player, msg);
    }

    public void reqComposeSummon(C2SSummonMsg.ComposeSummon msg) {
        LogicScriptsUtils.getISummonScript().reqComposeSummon(player, composePointMap, msg);
    }

    public void reqGetComposeInfo(C2SSummonMsg.GetComposeInfo msg) {
        LogicScriptsUtils.getISummonScript().reqGetComposeInfo(player, composePointMap, msg);
    }

    public void reqComposeFinish(C2SSummonMsg.ComposeFinish msg) {
        LogicScriptsUtils.getISummonScript().reqComposeFinish(player, composePointMap, msg);
    }

    public void reqHistoryRecord(C2SSummonMsg.ReqHistoryRecord msg) {
        LogicScriptsUtils.getISummonScript().reqHistoryRecord(player, historyRecordKV, msg);
    }

    @Override
    public void tick() {
        LogicScriptsUtils.getISummonScript().tick(player, composePointMap);
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounterFD() {
        return counterFD;
    }
    
    public void setCounterFD(int counterFD) {
        this.counterFD = counterFD;
    }
    
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<Integer, HistoryRecord> getHistoryRecord() {
        return historyRecordKV;
    }
}
