package logic.summon;

import java.util.Map;

import org.game.protobuf.c2s.C2SSummonMsg;

import logic.character.bean.Player;
import script.IScript;

public abstract class ISummonScript implements IScript {
    protected abstract void reqSummon(Player player, C2SSummonMsg.Summon msg);

    protected abstract void reqGetComposeInfo(Player player, Map<Integer, ComposePoint> composePointMap, C2SSummonMsg.GetComposeInfo msg);

    protected abstract void reqComposeSummon(Player player, Map<Integer, ComposePoint> composePointMap, C2SSummonMsg.ComposeSummon msg);
    
    protected abstract void reqComposeFinish(Player player, Map<Integer, ComposePoint> composePointMap, C2SSummonMsg.ComposeFinish msg);

    protected abstract void tick(Player player, Map<Integer, ComposePoint> composePointMap);
    
    protected abstract void createRoleInitialize(Map<Integer, ComposePoint> composePointMap);

    protected abstract void reqHistoryRecord(Player player, Map<Integer, HistoryRecord> historyRecordKV, C2SSummonMsg.ReqHistoryRecord msg);
    
    protected abstract void createPlayerInitialize(Map<Integer, ComposePoint> composePointMap);
}
