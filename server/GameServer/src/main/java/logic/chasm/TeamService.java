package logic.chasm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.game.protobuf.s2c.S2CTeamMsg;
import logic.character.bean.Player;
import logic.chasm.bean.TeamInfo;
import logic.constant.GameErrorCode;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;

public class TeamService {

    private static class DEFAULT {
        private static final TeamService provider = new TeamService();
    }

    public static TeamService getDefault() {
        return DEFAULT.provider;
    }

    private Map<Integer, Boolean> chasmStatus = new ConcurrentHashMap<>();

    public void checkChasmStatus() {
        LogicScriptsUtils.getTeamScript().checkChasmStatus(chasmStatus);
    }

    public Boolean getChasmStatus(int dungeonId) {
        return LogicScriptsUtils.getTeamScript().getChasmStatus(chasmStatus, dungeonId);
    }

    public void checkBeforeCreate(Player player, int teamType, int dungeonCid) {
        TeamInfo.ETeamType type = TeamInfo.ETeamType.getTeamType(teamType);
        switch (type) {
            case CHASM:
                checkChasm(player, dungeonCid);
                break;
            default:
                MessageUtils.throwCondtionError(GameErrorCode.CURRENT_DUNGEON_NOT_MATCH, "未知副本类型");
                break;
        }
    }

    private void checkChasm(Player player, int dungeonCid) {
        LogicScriptsUtils.getTeamScript().checkChasm(player, chasmStatus, dungeonCid);
    }

    public S2CTeamMsg.TeamInfo.Builder makeMsg(TeamInfo teamInfo) {
        return LogicScriptsUtils.getTeamScript().makeMsg(teamInfo);
    }

    public TeamInfo getTeamInfo(long teamId) {
        return LogicScriptsUtils.getTeamScript().getTeamInfo(teamId);
    }

    public void sendTeamInfoMsg(Player player, long teamId) {
        LogicScriptsUtils.getTeamScript().sendTeamInfoMsg(player, teamId);
    }

    public void sendCreateTeamInfoMsg(Player player, long teamId) {
        LogicScriptsUtils.getTeamScript().sendCreateTeamInfoMsg(player, teamId);
    }

    public boolean canApplyJoinTeam(int playerId) {
        return LogicScriptsUtils.getTeamScript().canApplyJoinTeam(playerId);
    }

    public boolean canApplyJoinTeam(int playerId, long lastApplyTime) {
        return LogicScriptsUtils.getTeamScript().canApplyJoinTeam(playerId, lastApplyTime);
    }

    public void exitChasmTeam(int playerId) {
        LogicScriptsUtils.getTeamScript().exitChasmTeam(playerId);
    }
}
