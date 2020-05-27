package javascript.logic.chasm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.MemberData;
import org.game.protobuf.c2s.C2SFightMsg.ReqEnterFight;
import org.game.protobuf.c2s.C2SFightMsg.ReqOperateFight;
import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CFightMsg.DataFrame;
import org.game.protobuf.s2c.S2CFightMsg.NetFrame;
import org.game.protobuf.s2c.S2CFightMsg.NotifyNetFrame;
import org.game.protobuf.s2c.S2CFightMsg.NotifyStartFight;
import org.game.protobuf.s2c.S2CFightMsg.OperateFrame;
import org.game.protobuf.s2c.S2CFightMsg.RespEnterFight;
import org.game.protobuf.s2c.S2CFightMsg.RespPullNetFrame;
import org.game.protobuf.s2c.S2CTeamMsg.RespJoinTeam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.ChasmDungeonCfgBean;
import data.bean.DiscreteDataCfgBean;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import kafka.service.F2GProductService;
import kafka.team.constants.TeamErrorCodeType;
import kafka.team.param.f2g.F2GAcceptTeamHandlerParam;
import kafka.team.param.f2g.F2GChasmFightReviveParam;
import kafka.team.param.f2g.F2GCreateTeamResultParam;
import kafka.team.param.f2g.F2GDissolveTeamParam;
import kafka.team.param.f2g.F2GEndFightParam;
import kafka.team.param.f2g.F2GErrorCodeHandlerParam;
import kafka.team.param.f2g.F2GExitTeamHandlerParam;
import kafka.team.param.f2g.F2GNotifyTeamRefreshParam;
import kafka.team.param.f2g.F2GTreatMemberHandlerParam;
import kafka.team.param.g2f.AcceptTeamHandlerParam;
import kafka.team.param.g2f.ChangeHeroParam;
import kafka.team.param.g2f.ChangeStatusHandlerParam;
import kafka.team.param.g2f.CreateTeamHandlerParam;
import kafka.team.param.g2f.MatchTeamHandlerParam;
import kafka.team.param.g2f.ReqChasmFightReviveParam;
import kafka.team.param.g2f.TreatMemberHandlerParam;
import logic.chasm.IChasmScript;
import logic.chasm.InnerHandler.LChangeHeroHandler;
import logic.chasm.InnerHandler.LCreateTeamHandler;
import logic.chasm.InnerHandler.LExitTeamHandler;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamInfo.ETeamStatus;
import logic.chasm.bean.TeamInfo.ETeamType;
import logic.chasm.bean.TeamMember;
import logic.chasm.bean.TeamTreatType;
import logic.chasm.handler.DestroyFightRoomHandler;
import logic.constant.EScriptIdDefine;
import logic.constant.FightConstant;
import logic.constant.IChannelConstants;
import logic.support.MessageUtils;
import net.Session;
import net.kcp.KcpOnUdp;
import net.kcp.Transfer;
import net.kcp.constant.KcpConstant;
import redis.FightRedisOper;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import room.FightRoom;
import room.FightRoom.Status;
import room.FightRoomManager;
import server.FightServer;
import server.ServerConfig;
import thread.FightRoomPrepareProcessor;
import thread.FightRoomProcessor;
import thread.FightRoomProcessorManager;
import thread.TeamProcessor;
import thread.TeamProcessorManager;
import utils.ExceptionEx;
import utils.ToolMap;
import utils.snowflake.IDGenerator;

public class ChasmScript extends IChasmScript {
    private static final Logger LOGGER = Logger.getLogger(ChasmScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.CHASM_DUNGEON_SCRIPT.Value();
    }

    @Override
    public void createTeam(CreateTeamHandlerParam json, TeamProcessor teamProcessor) throws Exception {
        long teamId = IDGenerator.getDefault().nextId();
        TeamInfo info = createTeam(json.getPlayerId(),
                json.getName(), json.getLevel(), json.getHeroCid(), json.getSkinCid(),
                json.getGameServerId(), teamId, json.getDungeonCid());
        info.setProcessorId(teamProcessor.getProcessorId());
        teamProcessor.addRoom(info);
        try (Jedis jedis = RedisServices
                .getRedisService(ERedisType.Fight.getType()).getJedis()) {

            jedis.hmset(RedisOper.TEAM_PREFIX + info.getTeamId(), info.toHash());
            jedis.expire(RedisOper.TEAM_PREFIX + info.getTeamId(), 1800);
        }
        F2GCreateTeamResultParam result = new F2GCreateTeamResultParam();
        result.setPlayerId(json.getPlayerId());
        result.setTeamId(teamId);
        F2GProductService.getDefault().sendMsg(json.getGameServerId(), result);
    }

    @Override
    public TeamInfo createTeam(int leaderId, String leaderName, int level, int heroCid, int skinCid,
            int gameServerId, long teamId, int dungeonCid) {
        Map<Integer, TeamMember> members = new HashMap<>();
        TeamMember member =
                new TeamMember(leaderId, leaderName, level, heroCid, skinCid, gameServerId);
        members.put(member.getPid(), member);
        // 获得战斗服的ip id port
        int serverId = ServerConfig.getInstance().getServerId();
        String serverIp = ServerConfig.getInstance().getExternalIp();
        int serverPort = ServerConfig.getInstance().getClientListenPort();

        TeamInfo info = new TeamInfo(teamId, leaderId, members, ETeamType.CHASM, dungeonCid,
                serverId, serverIp, serverPort, RandomUtil.randomInt());
        return info;
    }

    @Override
    public void changeStatus(ChangeStatusHandlerParam json, TeamInfo teamInfo) {
        try (Jedis jedis = RedisServices
                .getRedisService(ERedisType.Fight.getType()).getJedis()) {
            teamInfo.setStatus(ETeamStatus.getTeamStatus(json.getStatus()));
            jedis.hset(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), "status",
                    String.valueOf(json.getStatus()));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        refreshTeam(teamInfo);
    }

    @Override
    public void refreshTeam(TeamInfo teamInfo) {
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            F2GNotifyTeamRefreshParam result = new F2GNotifyTeamRefreshParam();
            result.setPlayerId(teamMember.getPid());
            result.setTeamId(teamInfo.getTeamId());
            // 要给全队所有人发
            try {
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    public void exitTeam(int playerId, TeamInfo teamInfo, TeamProcessor teamProcessor) {
        boolean isEmpty = false;
        TeamMember member = teamInfo.removeMember(playerId);
        if (member == null)
            return;
        if (teamInfo.getMemberNum() <= 0) {
            isEmpty = true;
            teamProcessor.removeRoom(teamInfo);
        } else if (playerId == teamInfo.getLeaderId()) {
            teamInfo.changeLeader();
        }
        if (isEmpty) {
            removeTeamRedis(teamInfo.getTeamId());
        } else {
            modifyTeamRedis(teamInfo);
        }

        // 发离开队伍给该玩家
        F2GExitTeamHandlerParam result = new F2GExitTeamHandlerParam();
        result.setPlayerId(playerId);
        result.setTeamId(teamInfo.getTeamId());
        result.setLeaver(playerId);
        try {
            F2GProductService.getDefault().sendMsg(member.getServerId(), result);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        // 通知队伍剩余成员有人离队
        if (!isEmpty) {
            for (TeamMember teamMember : teamInfo.getMembers().values()) {
                try {
                    result = new F2GExitTeamHandlerParam();
                    result.setPlayerId(teamMember.getPid());
                    result.setTeamId(teamInfo.getTeamId());
                    result.setLeaver(playerId);
                    F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        }
    }

    @Override
    public void treatMember(TreatMemberHandlerParam json, TeamInfo teamInfo, TeamProcessor teamProcessor) {
        if (json.getType() == TeamTreatType.KICK.getType()) {
            kickTeam(json, teamInfo, teamProcessor);
        } else if (json.getType() == TeamTreatType.PROMOTE.getType()) {
            promote(json, teamInfo);
        }
    }

    @Override
    public void promote(TreatMemberHandlerParam json, TeamInfo teamInfo) {
        if (json.getPlayerId() != teamInfo.getLeaderId()
                || json.getPlayerId() == json.getTargetPid()) {
            return;
        }
        if (!teamInfo.changeLeader(json.getTargetPid()))
            return;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
            jedis.hset(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), "leaderId",
                    String.valueOf(json.getTargetPid()));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            try {
                F2GTreatMemberHandlerParam result = new F2GTreatMemberHandlerParam();
                result.setPlayerId(teamMember.getPid());
                result.setTeamId(json.getTeamId());
                result.setType(json.getType());
                result.setOperatedPid(teamInfo.getLeaderId());
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    public void kickTeam(TreatMemberHandlerParam json, TeamInfo teamInfo,
            TeamProcessor teamProcessor) {
        if (json.getPlayerId() != teamInfo.getLeaderId()
                || json.getPlayerId() == json.getTargetPid()) {
            return;
        }
        if (teamInfo.getMembers().size() < 2)
            return;
        TeamMember member = teamInfo.removeMember(json.getTargetPid());
        if (member == null)
            return;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
            jedis.hmset(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), teamInfo.toHash());
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        F2GTreatMemberHandlerParam result = new F2GTreatMemberHandlerParam();
        result.setPlayerId(json.getTargetPid());
        result.setOperatedPid(json.getTargetPid());
        result.setTeamId(json.getTeamId());
        result.setType(json.getType());
        try {
            F2GProductService.getDefault().sendMsg(member.getServerId(), result);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        // 通知队伍剩余成员有人离队
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            try {
                result = new F2GTreatMemberHandlerParam();
                result.setPlayerId(teamMember.getPid());
                result.setOperatedPid(json.getTargetPid());
                result.setTeamId(json.getTeamId());
                result.setType(json.getType());
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    // 从创建队伍申请列表的redis中取出信息 并创建队伍
    @Override
    public void createTeamTick() {
        int teamMax = ServerConfig.getInstance().getMaxFightNum();
        int teamNum = TeamProcessorManager.getInstance().getTeamNum();
        // 队伍数达到上限，不再在本服创建队伍
        if (teamNum >= teamMax)
            return;
        long currentTime = System.currentTimeMillis();
        DiscreteDataCfgBean timeoutCfgBean = GameDataManager.getDiscreteDataCfgBean(21005);
        int timeout = ToolMap.getInt("time", timeoutCfgBean.getData(), 15);
        // 提前5秒超时，容错处理
        long endTime = currentTime - (timeout - 5) * 1000;
        List<String> entrants = null;
        try {
            entrants = FightRedisOper.createTeam(RedisOper.TEAM_CREATE_PREFIX, endTime, 1);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        if (entrants == null || entrants.size() == 0)
            return;
        int playerId = Integer.valueOf(entrants.iterator().next());
        CreateTeamHandlerParam param = null;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
            String objStr = jedis.get(RedisOper.TEAM_LEADER_PREFIX + playerId);
            jedis.del(RedisOper.TEAM_LEADER_PREFIX + playerId);
            param = JSONObject.parseObject(objStr, CreateTeamHandlerParam.class);
            jedis.expire(RedisOper.TEAM_TIME_PREFIX + param.getPlayerId(), 1800);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().chooseLineBySequence();
        teamProcessor.executeHandler(new LCreateTeamHandler(param, teamProcessor));
    }

    @Override
    public void singleMatchTick(AtomicInteger interval) {
        int teamMax = ServerConfig.getInstance().getMaxFightNum();
        int teamNum = TeamProcessorManager.getInstance().getTeamNum();
        // 队伍数达到上限就不再进行单人匹配
        if (teamNum >= teamMax)
            return;
        int teamMateMax = ServerConfig.getInstance().getTeamMaxMate();
        int intevalMate = ServerConfig.getInstance().getIntevalMate();
        // 队伍数达到一定数量后，等待intevalMate次后在匹配
        if (teamNum >= teamMateMax) {
            if (interval.get() >= intevalMate) {
                interval.set(0);
            } else {
                interval.incrementAndGet();
                return;
            }
        }

        for (ChasmDungeonCfgBean bean : GameDataManager.getChasmDungeonCfgBeans()) {
            int dungeonId = bean.getId();
            List<String> entrants = null;
            long endTime = getMatchEndTime();
            try {
                entrants =
                        FightRedisOper.match(RedisOper.TEAM_DUNGEON_PREFIX + dungeonId, endTime, 2);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                return;
            }
            if (entrants == null || entrants.size() == 0)
                continue;
            try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
                for(String entrant : entrants) {
                    jedis.expire(RedisOper.TEAM_TIME_PREFIX + entrant, 1800);
                }
            }
            int leaderId = Integer.valueOf(entrants.iterator().next());
            Map<Integer, TeamMember> members = new HashMap<>();
            // 获得战斗服的ip id port
            int serverId = ServerConfig.getInstance().getServerId();
            String serverIp = ServerConfig.getInstance().getExternalIp();
            int serverPort = ServerConfig.getInstance().getClientListenPort();
            long teamId;
            try {
                teamId = IDGenerator.getDefault().nextId();
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                return;
            }
            TeamInfo teamInfo = new TeamInfo(teamId, leaderId, members, ETeamType.CHASM, dungeonId,
                    serverId, serverIp, serverPort, RandomUtil.randomInt());
            teamInfo.setStatus(ETeamStatus.OPEN_AUTO_MATCH);
            TeamProcessor processor =
                    (TeamProcessor) TeamProcessorManager.getInstance().chooseLineBySequence();
            teamInfo.setProcessorId(processor.getProcessorId());
            joinTeam(teamInfo, entrants);
            modifyTeamRedis(teamInfo);
            try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
                jedis.expire(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), 1800);
            }
            notifyAcceptTeam(teamInfo, entrants);
            processor.addRoom(teamInfo);
        }
    }

    @Override
    public void modifyTeamRedis(TeamInfo teamInfo) {
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
            jedis.hmset(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), teamInfo.toHash());
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
    }

    @Override
    public void removeTeamRedis(long teamId) {
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
            jedis.del(RedisOper.TEAM_PREFIX + teamId);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dismissTeam(TeamInfo teamInfo) {
        TeamProcessorManager.getInstance().getRoomProcessor(teamInfo.getProcessorId())
                .removeRoom(teamInfo);;
        removeTeamRedis(teamInfo.getTeamId());
    }

    @Override
    public void acceptTeam(AcceptTeamHandlerParam json, TeamProcessor teamProcessor) {
        TeamInfo teamInfo = teamProcessor.getRoom(json.getTeamId());
        if (teamInfo == null) {
            sendErrorCode(json.getPlayerId(), TeamErrorCodeType.TEAM_NOT_EXIST,
                    json.getGameServerId());
            return;
        }
        if (teamInfo.getStatus() == ETeamStatus.FIGHTING) {
            LOGGER.error("teamInfo is fighting!teamId=" + json.getTeamId());
            sendErrorCode(json.getPlayerId(), TeamErrorCodeType.FIGHTING, json.getGameServerId());
            return;
        }
        if (getFreeNum(teamInfo.getDungeonCid(),
                teamInfo.getMemberNum()) <= 0) {
            // 人数已满
            sendErrorCode(json.getPlayerId(), TeamErrorCodeType.TEAM_FULL, json.getGameServerId());
            return;
        }
        joinTeam(teamInfo, json.getPlayerId(), json.getName(),
                json.getLevel(), json.getHeroCid(), json.getSkinCid(),
                json.getGameServerId());
        modifyTeamRedis(teamInfo);
        notifyAcceptTeam(teamInfo, json.getPlayerId());
    }

    @Override
    public void sendErrorCode(int playerId, int status, int gameServerId) {
        F2GErrorCodeHandlerParam errorCode = new F2GErrorCodeHandlerParam();
        errorCode.setPlayerId(playerId);
        errorCode.setCode(RespJoinTeam.MsgID.eMsgID_VALUE);
        errorCode.setStatus(status);
        try {
            F2GProductService.getDefault().sendMsg(gameServerId, errorCode);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void changeHero(ChangeHeroParam json) {
        long teamId = json.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            LOGGER.error("teamInfo not exist!teamId=" + teamId);
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new LChangeHeroHandler(teamInfo, json));
    }

    @Override
    public void teamMatchTick(Map<Long, TeamInfo> rooms, TeamProcessor teamProcessor) {
        // 如果房间在准备中 人数不够 则往里面加人
        DiscreteDataCfgBean discreteData = GameDataManager.getDiscreteDataCfgBean(21004);
        int teamTimeout = ToolMap.getInt("time", discreteData.getData(), 600) * 1000;
        for (TeamInfo teamInfo : rooms.values()) {
            if(teamInfo.getStatus() != ETeamStatus.FIGHTING) {
                if((System.currentTimeMillis() - teamInfo.getCreateTime()) >= teamTimeout) {
                    dissolveTeam(teamInfo, teamProcessor);
                    continue;
                }
            }
            
            if (teamInfo.getStatus() != ETeamStatus.OPEN_AUTO_MATCH)
                continue;
            int freeNum = getFreeNum(teamInfo.getDungeonCid(),
                    teamInfo.getMemberNum());
            if (freeNum <= 0) {
                continue;
            }

            long endTime = getMatchEndTime();
            List<String> entrants = null;
            try {
                entrants = FightRedisOper.match(
                        RedisOper.TEAM_DUNGEON_PREFIX + teamInfo.getDungeonCid(), endTime, 1);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                continue;
            }
            if (entrants == null || entrants.size() == 0)
                continue;
            try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
                for(String entrant : entrants) {
                    jedis.expire(RedisOper.TEAM_TIME_PREFIX + entrant, 1800);
                }
            }
            joinTeam(teamInfo, entrants);
            modifyTeamRedis(teamInfo);
            notifyAcceptTeam(teamInfo, entrants);
        }
    }

    @Override
    public void notifyAcceptTeam(TeamInfo teamInfo, int entrant) {
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            F2GAcceptTeamHandlerParam result = new F2GAcceptTeamHandlerParam();
            result.setPlayerId(teamMember.getPid());
            result.setEntrant(entrant == teamMember.getPid());
            result.setTeamId(teamInfo.getTeamId());
            // 要通知全队这个人加入了队伍 全队可能所有人不在同一个服务器
            try {
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    private void notifyAcceptTeam(TeamInfo teamInfo, List<String> entrants) {
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            F2GAcceptTeamHandlerParam result = new F2GAcceptTeamHandlerParam();
            result.setPlayerId(teamMember.getPid());
            boolean isEntrant = false;
            for (String entrant : entrants) {
                isEntrant = entrant.equals("" + teamMember.getPid());
                if (isEntrant)
                    break;
            }
            result.setEntrant(isEntrant);
            result.setTeamId(teamInfo.getTeamId());
            // 要通知全队这个人加入了队伍 全队可能所有人不在同一个服务器
            try {
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    private long getMatchEndTime() {
        int intervalSecond =
                (int) GameDataManager.getDiscreteDataCfgBean(17001).getData().get("time");
        int delayTime =
                (int) GameDataManager.getDiscreteDataCfgBean(17001).getData().get("delayTime");
        long now = new Date().getTime();
        return now - intervalSecond * 1000 + delayTime * 1000;
    }

    @Override
    public void joinTeam(TeamInfo teamInfo, List<String> entrants) {
        if (entrants == null || entrants.size() == 0)
            return;
        for (String playerIdStr : entrants) {
            int playerId = Integer.valueOf(playerIdStr);
            MatchTeamHandlerParam param = null;
            try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
                String objStr = jedis.get(RedisOper.TEAM_MATCH_PREFIX + playerId);
                jedis.del(RedisOper.TEAM_MATCH_PREFIX + playerId);
                param = JSONObject.parseObject(objStr, MatchTeamHandlerParam.class);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                continue;
            }
            if (param == null)
                continue;
            joinTeam(teamInfo, playerId, param.getName(), param.getLevel(), param.getHeroCid(),
                    param.getSkinCid(), param.getGameServerId());
        }
    }

    @Override
    public void joinTeam(TeamInfo teamInfo, int playerId, String name, int level, int heroCid,
            int skinCid, int gameServerId) {
        if (getFreeNum(teamInfo.getDungeonCid(),
                teamInfo.getMemberNum()) <= 0) {
            // 人数已满
            return;
        }
        TeamMember member = new TeamMember(playerId, name, level, heroCid, skinCid, gameServerId);
        teamInfo.getMembers().put(member.getPid(), member);
    }

    @Override
    public int getFreeNum(int chasmDungeonId, int memberNum) {
        int freeNum = getMaxNum(chasmDungeonId) - memberNum;
        return (freeNum < 0) ? 0 : freeNum;
    }

    private int getMaxNum(int chasmDungeonId) {
        return GameDataManager.getChasmDungeonCfgBean(chasmDungeonId).getCapacity();
    }

    @Override
    public void reqFightBeginHandler(Map<String, String> teamInfoMap) {
        TeamInfo teamInfo = new TeamInfo(teamInfoMap);
        FightRoom fightRoom = new FightRoom(teamInfo);
        FightRoomProcessor fightRoomProcessor = (FightRoomProcessor) FightRoomProcessorManager
                .getInstance().getRoomProcessor(fightRoom.getProcessorId());
        FightRoomManager.register(fightRoom);
        fightRoomProcessor.addRoom(fightRoom);
    }

    @Override
    public void reqEnterFight(Session session, ReqEnterFight msg) {
        String fightIdStr = msg.getFightId();
        int pid = msg.getPid();

        FightRoom fightRoom = session.getFightRoom();
        if (fightRoom != null && !fightRoom.isDestroy()
                && fightRoom.getStatus() != Status.FIGHTING) {
            TeamInfo teamInfo = fightRoom.getTeamInfo();
            Map<Integer, TeamMember> members = teamInfo.getMembers();
            if (members.containsKey(pid)) {
                TeamMember teamMember = members.get(pid);
                if (session.isKcp()) {
                    KcpOnUdp kcp = session.getKcpOnUdp();
                    teamMember.setKcp(kcp);
                    teamMember.setConnect(true);
                    kcp.setSessionValue(KcpConstant.FIGHT_ROOM, fightRoom.getId());
                    fightRoom.sessionIdRoleIds.put(String.valueOf(kcp.getKcp().getConv()), pid);
                } else {
                    ChannelHandlerContext ctx = session.getCtx();
                    teamMember.setCtx(ctx);
                    teamMember.setConnect(true);
                    Channel channel = ctx.channel();
                    channel.attr(IChannelConstants.FIGHT_ROOM).set(fightRoom.getId());
                    ChannelId channelId = channel.id();
                    fightRoom.sessionIdRoleIds.put(channelId.asShortText(), pid);
                    ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS)
                            .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});
                    ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS)
                            .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});
                }

                fightRoom.setStatus(FightRoom.Status.READY);

                boolean isReady = true;
                for (TeamMember member : members.values()) {
                    if (member.isConnect() == false) {
                        isReady = false;
                        break;
                    }
                }
                
                if (isReady) {
                    fightRoom.setStatus(FightRoom.Status.FIGHTING);
                    NotifyStartFight.Builder notifyStartFightBuilder =
                            NotifyStartFight.newBuilder();
                    for (TeamMember member : members.values()) {
                        if (member.isConnect()) {
                            notifyStartFightBuilder.addPids(member.getPid());
                        }
                    }
                    fightRoom.sendToOnlinePlayer(notifyStartFightBuilder);
                } else {
                    long excessTime = fightRoom.getStatusTime() + fightRoom.getReadyTime() * 1000 - System.currentTimeMillis();
                    RespEnterFight.Builder respEnterFightBuilder = RespEnterFight.newBuilder();
                    respEnterFightBuilder.setExcessTime((int)(excessTime / 1000));
                    if (session.isKcp()) {
                        Transfer.send(teamMember.getKcp(), fightRoom.getFactory(), respEnterFightBuilder);
                    } else {
                        MessageUtils.send(teamMember.getCtx(), respEnterFightBuilder, fightRoom.getFactory());
                    }
                }
            } else {
                LOGGER.error(pid + "没有队伍");
            }
        } else {
            LOGGER.error("战斗房间不存在" + fightIdStr);
        }
    }

    @Override
    public void reqOperateFight(Session session,
            ReqOperateFight operate) {
        FightRoom fightRoom = session.getFightRoom();
        if (fightRoom == null || fightRoom.isDestroy()) {
            return;
        }
        S2CFightMsg.NetFrame.Builder currentFrame = fightRoom.getCurrentFrame();

        int pid;
        if(session.isKcp()) {
            KcpOnUdp kcp = session.getKcpOnUdp();
            int id = kcp.getKcp().getConv();
            pid = fightRoom.sessionIdRoleIds.get(String.valueOf(id));
        } else {
            ChannelHandlerContext ctx = session.getCtx();
            Channel channel = ctx.channel();
            ChannelId channelId = channel.id();
            String id = channelId.asShortText();
            pid = fightRoom.sessionIdRoleIds.get(id);
        }

        OperateFrame.Builder operateFrameBuilder = OperateFrame.newBuilder();
        operateFrameBuilder.setPid(pid);
        operateFrameBuilder.setKeyCode(operate.getKeyCode());
        operateFrameBuilder.setKeyEvent(operate.getKeyEvent());
        operateFrameBuilder.setKeyEventEx(operate.getKeyEventEx());
        currentFrame.addOperateFrame(operateFrameBuilder);
    }

    @Override
    public void reqChasmFightRevive(TeamInfo teamInfo, ReqChasmFightReviveParam param) {
        FightRoom fightRoom = FightRoomManager.getRoomByRoleId(param.getPid());
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        TeamMember teamMember = teamMembers.get(param.getPid());
        F2GChasmFightReviveParam resParam = new F2GChasmFightReviveParam();
        resParam.setPlayerId(param.getPid());
        if(fightRoom == null || fightRoom.isDestroy()) {
            resParam.setSuccess(false);
        } else {
            int reliveCount = teamMember.getReliveCount();
            ChasmDungeonCfgBean cfgBean =
                    GameDataManager.getChasmDungeonCfgBean(teamInfo.getDungeonCid());
            List<Map<Integer, Integer>> costMap = cfgBean.getReviveCost();
            
            if (reliveCount >= costMap.size()) {
                resParam.setSuccess(false);
            } else {
                resParam.setSuccess(true);
                resParam.setReliveCount(reliveCount);
                resParam.setDungeonCid(teamInfo.getDungeonCid());
                teamMember.setReliveCount(reliveCount + 1);
                try (Jedis jedis =
                        RedisServices.getRedisService(ERedisType.Fight.getType()).getJedis()) {
                    List<JSONObject> membersList = new ArrayList<>();
                    for (TeamMember member : teamMembers.values()) {
                        JSONObject itemJSONObj =
                                JSONObject.parseObject(JSON.toJSONString(member.toHash()));
                        membersList.add(itemJSONObj);
                    }
                    jedis.hset(RedisOper.TEAM_PREFIX + teamInfo.getTeamId(), "members",
                            JSON.toJSONString(membersList));
                }
            }
        }
        
        try {
            F2GProductService.getDefault().sendMsg(teamMember.getServerId(), resParam);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void reqExitFight(Session session) {
        FightRoom fightRoom = session.getFightRoom();
        if(fightRoom == null || fightRoom.isDestroy()) {
            return;
        }
        
        int pid;
        if(session.isKcp()) {
            KcpOnUdp kcp = session.getKcpOnUdp();
            int id = kcp.getKcp().getConv();
            pid = fightRoom.sessionIdRoleIds.get(String.valueOf(id));
        } else {
            ChannelHandlerContext ctx = session.getCtx();
            Channel channel = ctx.channel();
            ChannelId channelId = channel.id();
            String id = channelId.asShortText();
            pid = fightRoom.sessionIdRoleIds.get(id);
        }
        
        NetFrame.Builder currentFrame = fightRoom.getCurrentFrame();
        DataFrame.Builder dataFrameBuilder = DataFrame.newBuilder();
        dataFrameBuilder.setPid(pid);
        dataFrameBuilder.setAction(FightConstant.EXIT);
        currentFrame.addDataFrame(dataFrameBuilder.build());    
    }

    @Override
    public void roomTick(FightRoom fightRoom) {
        if (fightRoom.getStatus() == Status.LOADING
                && System.currentTimeMillis() - fightRoom.getStatusTime() > (fightRoom.getEnterTime() * 1000)) {
            // 销毁房间
            fightRoom.destroyRoom(false);
        } else if (fightRoom.getStatus() == Status.READY
                && System.currentTimeMillis() - fightRoom.getStatusTime() > (fightRoom.getReadyTime() * 1000)) {
            // 强制开始战斗
            fightRoom.setStatus(Status.FIGHTING);
            
            NotifyStartFight.Builder notifyStartFightBuilder = NotifyStartFight.newBuilder();
            TeamInfo teamInfo = fightRoom.getTeamInfo();
            Map<Integer, TeamMember> members = teamInfo.getMembers();
            for (TeamMember member : members.values()) {
                if (member.isConnect()) {
                    notifyStartFightBuilder.addPids(member.getPid());
                }
            }
            fightRoom.sendToOnlinePlayer(notifyStartFightBuilder);
        } else if (fightRoom.getStatus() == Status.FIGHTING) {
            // 同步帧操作
            if (System.currentTimeMillis()
                    - fightRoom.getLastSyncTime() >= FightRoom.SYNC_INTERVAL) {
                fightRoom.setLastSyncTime(System.currentTimeMillis());
                notifyCurrentFrame(fightRoom);
                fightRoom.setSyncCount(fightRoom.getSyncCount() + 1);
            }

            // 全体掉线或者战斗超时
            long fightTime = (long) (fightRoom.getSyncCount() * 66.67d);
            if (fightRoom.isAllOffline() || fightTime > (fightRoom.getBattleTime() + 10000)) {
                fightRoom.destroyRoom(false);
            }
        }
    }

    private void notifyCurrentFrame(FightRoom fightRoom) {
        NetFrame.Builder netFrame = fightRoom.getCurrentFrame();
        fightRoom.getNetFrames().add(netFrame.build());
        NetFrame.Builder netFrameBuilder = NetFrame.newBuilder();
        netFrameBuilder.setIndex(netFrame.getIndex() + 1);
        fightRoom.setCurrentFrame(netFrameBuilder);

        NotifyNetFrame.Builder notifyNetFrameBuilder = NotifyNetFrame.newBuilder();
        notifyNetFrameBuilder.setNetFrame(netFrame.build());
        fightRoom.sendToOnlinePlayer(notifyNetFrameBuilder);
    
        List<OperateFrame> operateFrames = netFrame.getOperateFrameList();
        List<DataFrame> dataFrames = netFrame.getDataFrameList();
        FightServer.NOTIFY_FRAME_DATA.addAndGet(operateFrames.size() + dataFrames.size());
    }

    @Override
    public void channelInactive(Session session) {
        FightRoom fightRoom = session.getFightRoom();
        TeamInfo teamInfo = fightRoom.getTeamInfo();

        int playerId;
        if(session.isKcp()) {
            KcpOnUdp kcp = session.getKcpOnUdp();
            int id = kcp.getKcp().getConv();
            playerId = fightRoom.sessionIdRoleIds.get(String.valueOf(id));
            
            TeamMember teamMember = teamInfo.getMember(playerId);
            teamMember.setKcp(null);
            teamMember.setConnect(false);
            fightRoom.sessionIdRoleIds.remove(String.valueOf(id));
        } else {
            ChannelHandlerContext ctx = session.getCtx();
            Channel channel = ctx.channel();
            ChannelId channelId = channel.id();
            String id = channelId.asShortText();
            playerId = fightRoom.sessionIdRoleIds.get(id);
            TeamMember teamMember = teamInfo.getMember(playerId);
            teamMember.setCtx(null);
            teamMember.setConnect(false);
            fightRoom.sessionIdRoleIds.remove(id);
        }

        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(
                new LExitTeamHandler(playerId, teamInfo.getTeamId(), teamProcessor));

        if (fightRoom.isAllOffline()) {
            fightRoom.destroyRoom(false);
        } else {
            NetFrame.Builder currentFrame = fightRoom.getCurrentFrame();
            DataFrame.Builder dataFrameBuilder = DataFrame.newBuilder();
            dataFrameBuilder.setPid(playerId);
            dataFrameBuilder.setAction(FightConstant.EXIT);
            currentFrame.addDataFrame(dataFrameBuilder.build());
        }
    }

    @Override
    public void reqEndFight(FightRoom fightRoom, C2SFightMsg.ReqEndFight reqEndFight) {
        if (fightRoom == null || fightRoom.isDestroy()) {
            return;
        }
        fightRoom.destroyRoom(true);

        TeamInfo teamInfo = fightRoom.getTeamInfo();
        boolean win = reqEndFight.getIsWin();
        fightRoom.addMemberWin(win);
        List<MemberData> memberDatas = reqEndFight.getMemberDataList();
        int fightTime = reqEndFight.getFightTime();

        MemberData mvp = memberDatas.get(0);
        int hurt = mvp.getHurt();
        for (MemberData memberData : memberDatas) {
            if (memberData.getHurt() > hurt) {
                mvp = memberData;
                hurt = memberData.getHurt();
            }
        }
        
        F2GEndFightParam param = new F2GEndFightParam();
        param.setWin(win);
        param.setFightTime(fightTime);
        param.setDungeonCid(teamInfo.getDungeonCid());
        param.setTeamId(teamInfo.getTeamId());
        for (MemberData memberData : memberDatas) {
            F2GEndFightParam.MemberData memberDataParam = new F2GEndFightParam.MemberData();
            memberDataParam.setPid(memberData.getPid());
            memberDataParam.setHurt(memberData.getHurt());
            if (memberData.getPid() == mvp.getPid()) {
                memberDataParam.setMvp(true);
            } else {
                memberDataParam.setMvp(false);
            }
            param.getMemberDatas().add(memberDataParam);
        }
        
        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
        for (Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
            TeamMember teamMember = entry.getValue();
            if(teamMember.isConnect()) {
                param.setPlayerId(teamMember.getPid());
                try {
                    F2GProductService.getDefault().sendMsg(teamMember.getServerId(), param);
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        }
    }

    @Override
    public void destroyRoom(FightRoom fightRoom, boolean isWin) {
        fightRoom.setDestroy(true);
        DestroyFightRoomHandler handler = new DestroyFightRoomHandler(fightRoom, isWin);
        FightRoomPrepareProcessor.getInstance().executeHandler(handler);
    }

    @Override
    public void reqReviveFight(Session session) {
        FightRoom fightRoom = session.getFightRoom();
        if(fightRoom == null || fightRoom.isDestroy()) {
            return;
        }
        
        NetFrame.Builder currentFrame = fightRoom.getCurrentFrame();
        
        int pid;
        if(session.isKcp()) {
            KcpOnUdp kcp = session.getKcpOnUdp();
            int id = kcp.getKcp().getConv();
            pid = fightRoom.sessionIdRoleIds.get(String.valueOf(id));
        } else {
            ChannelHandlerContext ctx = session.getCtx();
            Channel channel = ctx.channel();
            ChannelId channelId = channel.id();
            String id = channelId.asShortText();
            pid = fightRoom.sessionIdRoleIds.get(id);
        }
        DataFrame.Builder dataFrameBuilder = DataFrame.newBuilder();
        dataFrameBuilder.setPid(pid);
        dataFrameBuilder.setAction(FightConstant.REVIVE);
        currentFrame.addDataFrame(dataFrameBuilder.build());
    }
    
    @Override
    public void dissolveTeam(TeamInfo teamInfo, TeamProcessor teamProcessor) {
        // 通知队伍成员离队
        for (TeamMember teamMember : teamInfo.getMembers().values()) {
            try {
                F2GDissolveTeamParam result = new F2GDissolveTeamParam();
                result.setPlayerId(teamMember.getPid());
                F2GProductService.getDefault().sendMsg(teamMember.getServerId(), result);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
        teamProcessor.removeRoom(teamInfo);
        removeTeamRedis(teamInfo.getTeamId());
    }
    
    @Override
    public void reqPullNetFrame(Session session, C2SFightMsg.ReqPullNetFrame msg) {
        FightRoom fightRoom = session.getFightRoom();
        
        int fromIndex = msg.getFromIndex();
        
        List<NetFrame> netFrames = fightRoom.getNetFrames();
        int toIndex = netFrames.size();
        List<NetFrame> netFrameList = netFrames.subList(fromIndex, toIndex);;
        
        RespPullNetFrame.Builder respPullNetFrameBuilder = RespPullNetFrame.newBuilder();
        for(NetFrame frame : netFrameList) {
            respPullNetFrameBuilder.addNetFrames(frame);    
        }
        
        if(session.isKcp()) {
            KcpOnUdp kcp = session.getKcpOnUdp();
            Transfer.send(kcp, fightRoom.getFactory(), respPullNetFrameBuilder);
        } else {
            MessageUtils.send(session.getCtx(), respPullNetFrameBuilder, fightRoom.getFactory());
        }
    }
}
