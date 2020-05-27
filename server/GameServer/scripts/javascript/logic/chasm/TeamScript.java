package javascript.logic.chasm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CTeamMsg;
import org.game.protobuf.s2c.S2CTeamMsg.RespCreateTeam;
import org.game.protobuf.s2c.S2CTeamMsg.TeamInfo.Builder;
import data.GameDataManager;
import data.bean.ChasmDungeonCfgBean;
import kafka.service.G2FProductService;
import kafka.team.param.g2f.ExitTeamHandlerParam;
import logic.bag.BagManager;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.ITeamScript;
import logic.chasm.TeamDungeonManager;
import logic.chasm.TeamRedisService;
import logic.chasm.InnerHandler.LRsepEnterChasmHandler;
import logic.chasm.bean.ChasmInfo;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.constant.EFunctionType;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import thread.player.PlayerProcessorManager;
import utils.ExceptionEx;

public class TeamScript extends ITeamScript {
    private static final Logger LOGGER = Logger.getLogger(TeamScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TEAM_SCRIPT.Value();
    }

    @Override
    public void checkChasmStatus(Map<Integer, Boolean> chasmStatus) {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.TEAM_FIGHT)) {
            return;
        }
        List<ChasmDungeonCfgBean> cfgBeans = GameDataManager.getChasmDungeonCfgBeans();
        boolean isNotify = false;
        for (ChasmDungeonCfgBean cfgBean : cfgBeans) {
            if (!chasmStatus.containsKey(cfgBean.getId())) {
                chasmStatus.put(cfgBean.getId(), false);
            }
            int[] openWeeks = cfgBean.getOpenWeeks();
            int[] openTimes = cfgBean.getOpenTimes();
            int continuedTime = cfgBean.getContinuedTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            boolean isContain = false;
            for (int openWeek : openWeeks) {
                if (openWeek == today) {
                    isContain = true;
                    break;
                }
            }

            boolean isOpen = false;
            if (isContain) {
                long currentTime = System.currentTimeMillis();
                calendar.set(Calendar.DAY_OF_WEEK, today);
                for (int openTime : openTimes) {
                    calendar.set(Calendar.HOUR_OF_DAY, openTime);
                    long startTime = calendar.getTimeInMillis();
                    long deadline = startTime + continuedTime * 60 * 1000;
                    if (currentTime >= startTime && currentTime <= deadline) {
                        isOpen = true;
                        break;
                    }
                }
            }
            Boolean status = getChasmStatus(chasmStatus, cfgBean.getId());
            chasmStatus.put(cfgBean.getId(), isOpen);
            if (isOpen != status) {
                isNotify = true;
            }
        }
        
        if(isNotify) {
            List<Player> players = PlayerManager.getAllPlayers();
            for (Player player : players) {
                if (player != null && player.isOnline()) {
                    PlayerProcessorManager.getInstance().addPlayerHandler(player,
                            new LRsepEnterChasmHandler(player));
                }
            }
        }
    }

    @Override
    public Boolean getChasmStatus(Map<Integer, Boolean> chasmStatus, int dungeonId) {
        Boolean status = chasmStatus.get(dungeonId);
        if (status == null) {
            return false;
        } else {
            return status;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void checkChasm(Player player, Map<Integer, Boolean> chasmStatus, int dungeonCid) {
        ChasmDungeonCfgBean cfgBean = GameDataManager.getChasmDungeonCfgBean(dungeonCid);
        boolean isOpen = getChasmStatus(chasmStatus, cfgBean.getId());
        if (!isOpen) {
            MessageUtils.throwCondtionError(GameErrorCode.DUNGEON_LEVEL_IS_NOT_OPEN, "关卡未开启");
        }

        int[] levelLimit = cfgBean.getLvlLimit();
        if (player.getLevel() < levelLimit[0] || player.getLevel() > levelLimit[1]) {
            MessageUtils.throwCondtionError(GameErrorCode.PLAYER_LEVEL_IS_NOT_ENOUGH, "玩家等级不足");
        }

        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        ChasmInfo chasmInfo = teamDungeonManager.getChasmInfo(dungeonCid);
        if (chasmInfo.getFightCount() >= cfgBean.getFightCount()) {
            MessageUtils.throwCondtionError(GameErrorCode.FIGHT_COUNT_IS_UPPER_LIMIT, "没有挑战次数");
        }

        Map<Integer, Integer> costMap = cfgBean.getFightCost();
        BagManager bagManager = player.getBagManager();
        boolean isEnough = bagManager.enoughByTemplateId(costMap);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.GOODS_IS_NOT_ENOUGH, "玩家等级不足");
        }
    }

    @Override
    public Builder makeMsg(TeamInfo teamInfo) {
        S2CTeamMsg.TeamInfo.Builder teamBuilder = S2CTeamMsg.TeamInfo.newBuilder();
        teamBuilder.setTeamId(String.valueOf(teamInfo.getTeamId()));
        teamBuilder.setLeaderPid(teamInfo.getLeaderId());
        teamBuilder.setStatus(teamInfo.getStatus().getStatus());
        for (TeamMember member : teamInfo.getMembers().values()) {
            S2CTeamMsg.TeamMember.Builder memberBuilder = S2CTeamMsg.TeamMember.newBuilder();
            memberBuilder.setHeroCid(member.getHeroCid());
            memberBuilder.setPid(member.getPid());
            memberBuilder.setStatus(member.getStatus().getStatus());// 队员状态 1:空闲 2:准备中
            memberBuilder.setName(member.getName());
            memberBuilder.setPlv(member.getLevel());
            memberBuilder.setSkinCid(member.getSkinCid());
            teamBuilder.addMembers(memberBuilder);
        }
        return teamBuilder;
    }

    @Override
    public TeamInfo getTeamInfo(long teamId) {
        TeamInfo teamInfo = null;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            Map<String, String> teamInfoMap = jedis.hgetAll(RedisOper.TEAM_PREFIX + teamId);
            if (teamInfoMap == null || teamInfoMap.size() == 0) {
                LOGGER.error("teamInfoMap not exist! teamId=" + teamId);
                return null;
            }
            teamInfo = new TeamInfo(teamInfoMap);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return null;
        }
        return teamInfo;
    }

    @Override
    public void sendTeamInfoMsg(Player player, long teamId) {
        if (player == null || !player.isOnline())
            return;
        TeamInfo teamInfo = getTeamInfo(teamId);
        S2CTeamMsg.TeamInfo.Builder teamBuilder = makeMsg(teamInfo);
        MessageUtils.send(player, teamBuilder);
    }

    @Override
    public void sendCreateTeamInfoMsg(Player player, long teamId) {
        TeamInfo teamInfo = getTeamInfo(teamId);
        if (teamInfo == null) {
            // 队伍数据已经不存在了 但队伍信息还在 。要删除队伍信息.
            TeamRedisService.delPlayerTeamId(player.getPlayerId());
            TeamRedisService.delPlayerTeamTime(player.getPlayerId());
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_TEAM,
                    player.getPlayerId() + "有队伍id但没有队伍信息");
        }
        S2CTeamMsg.TeamInfo.Builder teamBuilder = makeMsg(teamInfo);
        RespCreateTeam.Builder builder = RespCreateTeam.newBuilder();
        builder.setTeam(teamBuilder);
        MessageUtils.send(player, builder);
    }

    @Override
    public boolean canApplyJoinTeam(int playerId) {
        long lastApplyTime = TeamRedisService.getPlayerTeamTime(playerId);
        // 找到策划配置的时间 和当前时间 做比较
        return canApplyJoinTeam(playerId, lastApplyTime);
    }

    @Override
    public boolean canApplyJoinTeam(int playerId, long lastApplyTime) {
        // 找到策划配置的时间 和当前时间 做比较
        int intervalSecond =
                (int) GameDataManager.getDiscreteDataCfgBean(17001).getData().get("time");
        return (lastApplyTime + intervalSecond * 1000) < new Date().getTime();
    }

    @Override
    public void exitChasmTeam(int playerId) {
        TeamRedisService.delPlayerMatch(playerId);
        long teamId = TeamRedisService.getPlayerTeamId(playerId);
        if (teamId != 0) {
            int serverId = TeamRedisService.getTeamServerId(teamId);
            ExitTeamHandlerParam param = new ExitTeamHandlerParam();
            param.setPlayerId(playerId);
            param.setTeamId(teamId);
            try {
                if (serverId != 0)
                    G2FProductService.getDefault().sendToFightServer(serverId, param);
            } catch (Exception e) {
                LOGGER.error("kafka退出深渊队伍错误" + ExceptionEx.e2s(e));
            }
            TeamRedisService.delPlayerTeamId(playerId);
        }
        TeamRedisService.delPlayerTeamTime(playerId);
    }
}
