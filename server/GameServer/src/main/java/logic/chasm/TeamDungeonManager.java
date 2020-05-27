package logic.chasm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqCreateTeam;
import org.game.protobuf.s2c.S2CChasmMsg;

import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.chasm.bean.ChasmInfo;
import logic.support.LogicScriptsUtils;

public class TeamDungeonManager extends PlayerBaseFunctionManager
        implements ICreatePlayerInitialize, IRoleJsonConverter, ITick {
    /** 深渊关卡信息 */
    private Map<Integer, ChasmInfo> chasmInfos = new HashMap<>();
    /** 跨天时间 */
    private long acrossDayTime = getRefreshTime();

    /**
     * 请求创建队伍
     * 
     * @param msg
     */
    public void reqCreateTeam(ReqCreateTeam msg) {
        LogicScriptsUtils.getChasmScript().reqCreateTeam(player, msg);
    }

    public void reqMatchTeam(int dungeonCid, int teamType) {
        LogicScriptsUtils.getChasmScript().reqMatchTeam(player, dungeonCid, teamType);
    }

    public void invitTeam(int targetPlayerId, String content) {
        LogicScriptsUtils.getChasmScript().invitTeam(player, targetPlayerId, content);
    }

    public void reqJoinTeam(Player player, C2STeamMsg.ReqJoinTeam msg) {
        LogicScriptsUtils.getChasmScript().reqJoinTeam(player, msg);
    }

    public void changeHero(int playerId, long teamId, int heroCid, int skinCid) {
        LogicScriptsUtils.getChasmScript().changeHero(playerId, teamId, heroCid, skinCid);
    }

    public void changeStatus(C2STeamMsg.ReqChangeTeamStatus msg) {
        LogicScriptsUtils.getChasmScript().changeStatus(player, msg);
    }

    public void changeMemberStatus(int playerId, long teamId, int status) {
        LogicScriptsUtils.getChasmScript().changeMemberStatus(playerId, teamId, status);
    }

    public void treatMember(C2STeamMsg.ReqTreatMember msg) {
        LogicScriptsUtils.getChasmScript().treatMember(player, msg);
    }

    public void exitTeam(int playerId, long teamId) {
        LogicScriptsUtils.getChasmScript().exitTeam(playerId, teamId);
    }

    @Override
    public void createPlayerInitialize() {
        LogicScriptsUtils.getChasmScript().createPlayerInitialize(chasmInfos);
    }

    @Override
    public void tick() {
        LogicScriptsUtils.getChasmScript().tick(player, chasmInfos);
    }

    private long getRefreshTime() {
        return LogicScriptsUtils.getChasmScript().getRefreshTime();
    }

    public List<S2CChasmMsg.ChasmInfo> buildChasmInfos() {
        return LogicScriptsUtils.getChasmScript().buildChasmInfos(chasmInfos);
    }
    
    public ChasmInfo getChasmInfo(int dungeonId) {
        return chasmInfos.get(dungeonId);
    }
    
    public long getAcrossDayTime() {
        return acrossDayTime;
    }

    public void setAcrossDayTime(long acrossDayTime) {
        this.acrossDayTime = acrossDayTime;
    }
}
