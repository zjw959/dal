package logic.chasm;

import java.util.Map;

import org.game.protobuf.s2c.S2CTeamMsg;

import logic.character.bean.Player;
import logic.chasm.bean.TeamInfo;
import script.IScript;

public abstract class ITeamScript implements IScript {
    public abstract void checkChasmStatus(Map<Integer, Boolean> chasmStatus);

    public abstract Boolean getChasmStatus(Map<Integer, Boolean> chasmStatus, int dungeonId);

    public abstract void checkChasm(Player player, Map<Integer, Boolean> chasmStatus,
            int dungeonCid);

    public abstract S2CTeamMsg.TeamInfo.Builder makeMsg(TeamInfo teamInfo);

    public abstract TeamInfo getTeamInfo(long teamId);

    public abstract void sendTeamInfoMsg(Player player, long teamId);

    public abstract void sendCreateTeamInfoMsg(Player player, long teamId);

    public abstract boolean canApplyJoinTeam(int playerId);

    public abstract boolean canApplyJoinTeam(int playerId, long lastApplyTime);

    public abstract void exitChasmTeam(int playerId);
}
