package logic.chasm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqOperateFight;
import kafka.team.param.g2f.AcceptTeamHandlerParam;
import kafka.team.param.g2f.ChangeHeroParam;
import kafka.team.param.g2f.ChangeStatusHandlerParam;
import kafka.team.param.g2f.CreateTeamHandlerParam;
import kafka.team.param.g2f.ReqChasmFightReviveParam;
import kafka.team.param.g2f.TreatMemberHandlerParam;
import logic.chasm.bean.TeamInfo;
import net.Session;
import room.FightRoom;
import script.IScript;
import thread.TeamProcessor;

public abstract class IChasmScript implements IScript {
    public abstract void createTeam(CreateTeamHandlerParam json, TeamProcessor teamProcessor) throws Exception;
    
    public abstract TeamInfo createTeam(int leaderId, String leaderName, int level, int heroCid,
            int skinCid, int gameServerId, long teamId, int dungeonCid);

    public abstract void joinTeam(TeamInfo teamInfo, List<String> entrants);

    public abstract void joinTeam(TeamInfo teamInfo, int playerId, String name, int level,
            int heroCid, int skinCid, int gameServerId);

    public abstract void modifyTeamRedis(TeamInfo teamInfo);

    public abstract void removeTeamRedis(long teamId);

    public abstract void notifyAcceptTeam(TeamInfo teamInfo, int entrant);

    public abstract void refreshTeam(TeamInfo teamInfo);

    public abstract void dismissTeam(TeamInfo teamInfo);

    public abstract void exitTeam(int playerId, TeamInfo teamInfo, TeamProcessor teamProcessor);

    public abstract int getFreeNum(int chasmDungeonId, int memberNum);

    public abstract void changeStatus(ChangeStatusHandlerParam json, TeamInfo teamInfo);

    public abstract void treatMember(TreatMemberHandlerParam json, TeamInfo teamInfo, TeamProcessor teamProcessor);

    public abstract void kickTeam(TreatMemberHandlerParam json, TeamInfo teamInfo,
            TeamProcessor teamProcessor);

    public abstract void promote(TreatMemberHandlerParam json, TeamInfo teamInfo);

    public abstract void singleMatchTick(AtomicInteger interval);
    
    public abstract void createTeamTick();

    public abstract void acceptTeam(AcceptTeamHandlerParam json, TeamProcessor teamProcessor);
    
    public abstract void sendErrorCode(int playerId, int status, int gameServerId);
    
    public abstract void changeHero(ChangeHeroParam json);
    
    public abstract void teamMatchTick(Map<Long, TeamInfo> rooms, TeamProcessor teamProcessor);
    
    public abstract void reqFightBeginHandler(Map<String, String> teamInfoMap);

    public abstract void reqEnterFight(Session session, C2SFightMsg.ReqEnterFight msg);

    public abstract void reqOperateFight(Session session, ReqOperateFight operate);

    public abstract void reqChasmFightRevive(TeamInfo teamInfo, ReqChasmFightReviveParam param);

    public abstract void reqExitFight(Session session);

    public abstract void roomTick(FightRoom fightRoom);

    public abstract void channelInactive(Session session);

    public abstract void reqEndFight(FightRoom fightRoom, C2SFightMsg.ReqEndFight reqEndFight);
    
    public abstract void destroyRoom(FightRoom fightRoom, boolean isWin);
    
    public abstract void reqReviveFight(Session session);
    
    public abstract void dissolveTeam(TeamInfo teamInfo, TeamProcessor teamProcessor);

    public abstract void reqPullNetFrame(Session session, C2SFightMsg.ReqPullNetFrame msg);
}
