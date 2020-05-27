package logic.chasm;

import java.util.List;
import java.util.Map;
import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.s2c.S2CChasmMsg;
import kafka.team.param.f2g.F2GAcceptTeamHandlerParam;
import kafka.team.param.f2g.F2GChasmFightReviveParam;
import kafka.team.param.f2g.F2GCreateTeamResultParam;
import kafka.team.param.f2g.F2GEndFightParam;
import kafka.team.param.f2g.F2GTreatMemberHandlerParam;
import kafka.team.param.g2f.ResChasmStartFightParam;
import logic.character.bean.Player;
import logic.chasm.bean.ChasmInfo;
import script.IScript;

public abstract class IChasmScript implements IScript {
    public abstract void reqCreateTeam(Player player, C2STeamMsg.ReqCreateTeam msg);

    public abstract void reqMatchTeam(Player player, int dungeonCid, int teamType);

    public abstract void invitTeam(Player player, int targetPlayerId, String content);

    public abstract void reqJoinTeam(Player player, C2STeamMsg.ReqJoinTeam msg);

    public abstract void changeHero(int playerId, long teamId, int heroCid, int skinCid);

    public abstract void changeStatus(Player player, C2STeamMsg.ReqChangeTeamStatus msg);

    public abstract void changeMemberStatus(int playerId, long teamId, int status);

    public abstract void treatMember(Player player, C2STeamMsg.ReqTreatMember msg);

    public abstract void exitTeam(int playerId, long teamId);

    public abstract void createPlayerInitialize(Map<Integer, ChasmInfo> chasmInfos);

    public abstract void tick(Player player, Map<Integer, ChasmInfo> chasmInfos);
    
    public abstract long getRefreshTime();
    
    public abstract List<S2CChasmMsg.ChasmInfo> buildChasmInfos(Map<Integer, ChasmInfo> chasmInfos);

    public abstract void reqCancelMatch(Player player);
    
    public abstract void createTeamF2G(F2GCreateTeamResultParam json);

    public abstract void treatMemberF2G(F2GTreatMemberHandlerParam json);

    public abstract void acceptTeamF2G(F2GAcceptTeamHandlerParam json);

    public abstract void reqChasmStartFight(Player player);

    public abstract void startChasmFightF2G(ResChasmStartFightParam param);
    
    public abstract void reqChasmFightRevive(Player player);
    
    public abstract void chasmFightReviveF2G(F2GChasmFightReviveParam param);

    public abstract void endFightOnline(Player player, F2GEndFightParam param);
    
    public abstract void endFightOffline(int playerId, F2GEndFightParam param);

    public abstract void dissolveTeam(Player player);
}
