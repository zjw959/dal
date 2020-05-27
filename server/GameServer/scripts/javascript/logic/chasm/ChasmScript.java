package javascript.logic.chasm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqCreateTeam;
import org.game.protobuf.c2s.C2STeamMsg.TeamFeature;
import org.game.protobuf.s2c.S2CChasmMsg;
import org.game.protobuf.s2c.S2CChasmMsg.FightPlayer;
import org.game.protobuf.s2c.S2CChasmMsg.ResqChasmFightRevive;
import org.game.protobuf.s2c.S2CChasmMsg.RsepChasmStartFight;
import org.game.protobuf.s2c.S2CChasmMsg.RsepEnterChasm;
import org.game.protobuf.s2c.S2CFightMsg.FightResultdetails;
import org.game.protobuf.s2c.S2CFightMsg.RespEndFight;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import org.game.protobuf.s2c.S2CTeamMsg;
import org.game.protobuf.s2c.S2CTeamMsg.RespCancelMatch;
import org.game.protobuf.s2c.S2CTeamMsg.RespChangeHero;
import org.game.protobuf.s2c.S2CTeamMsg.RespJoinTeam;
import org.game.protobuf.s2c.S2CTeamMsg.RespLeaveTeam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.ChasmDungeonCfgBean;
import data.bean.DiscreteDataCfgBean;
import kafka.service.G2FProductService;
import kafka.service.G2GInviteTeamProductService;
import kafka.team.param.f2g.F2GAcceptTeamHandlerParam;
import kafka.team.param.f2g.F2GChasmFightReviveParam;
import kafka.team.param.f2g.F2GCreateTeamResultParam;
import kafka.team.param.f2g.F2GEndFightParam;
import kafka.team.param.f2g.F2GTreatMemberHandlerParam;
import kafka.team.param.f2g.F2GEndFightParam.MemberData;
import kafka.team.param.g2f.AcceptTeamHandlerParam;
import kafka.team.param.g2f.ChangeHeroParam;
import kafka.team.param.g2f.ChangeMemberStatusHandlerParam;
import kafka.team.param.g2f.ChangeStatusHandlerParam;
import kafka.team.param.g2f.CreateTeamHandlerParam;
import kafka.team.param.g2f.ExitTeamHandlerParam;
import kafka.team.param.g2f.MatchTeamHandlerParam;
import kafka.team.param.g2f.ReqChasmFightReviveParam;
import kafka.team.param.g2f.ReqChasmStartFightParam;
import kafka.team.param.g2f.ResChasmStartFightParam;
import kafka.team.param.g2f.ResChasmStartFightParam.ResultCode;
import kafka.team.param.g2f.TreatMemberHandlerParam;
import kafka.team.param.g2g.InviteTeamHandlerParam;
import logic.bag.BagManager;
import logic.character.PlayerManager;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.chasm.IChasmScript;
import logic.chasm.TeamDungeonManager;
import logic.chasm.TeamRedisService;
import logic.chasm.TeamService;
import logic.chasm.InnerHandler.LCreateTeamHandler;
import logic.chasm.bean.ChasmInfo;
import logic.chasm.bean.TeamCancelMatchType;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamInfo.ETeamStatus;
import logic.chasm.bean.TeamLeaveType;
import logic.chasm.bean.TeamMember;
import logic.chasm.bean.TeamTreatType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.hero.bean.Hero;
import logic.item.ItemPackageHelper;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import message.SMessage;
import redis.base.RedisCmd;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import server.ServerConfig;
import thread.log.LogProcessor;
import thread.player.PlayerProcessorManager;
import utils.DateEx;
import utils.ExceptionEx;
import utils.LocationServerUtil;
import utils.ToolMap;
public class ChasmScript extends IChasmScript {
    private static final Logger LOGGER = Logger.getLogger(ChasmScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.CHASM_DUNGEON_SCRIPT.Value();
    }

    @Override
    public void reqCreateTeam(Player player, ReqCreateTeam msg) {
        // 还不能匹配
        if (!TeamService.getDefault().canApplyJoinTeam(player.getPlayerId())) {
            long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
            if (teamId != 0) {
                // 返回客户端已有队伍信息
                TeamService.getDefault().sendCreateTeamInfoMsg(player, Long.valueOf(teamId));
            } else {
                // 队伍可能正在创建中
                MessageUtils.throwCondtionError(GameErrorCode.ALREADY_EXIST_TEAM,
                        player.getPlayerId() + "已存在队伍");
            }
            return;
        }    

        TeamFeature teamFeature = msg.getFeature();
        int teamType = teamFeature.getTeamType();
        int dungeonCid = teamFeature.getDungeonCid();
        // 检查能否打改关卡
        TeamService.getDefault().checkBeforeCreate(player, teamType, dungeonCid);

        DiscreteDataCfgBean timeoutCfgBean = GameDataManager.getDiscreteDataCfgBean(21005);
        int timeout = ToolMap.getInt("time", timeoutCfgBean.getData(), 15);
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.set(RedisOper.TEAM_TIME_PREFIX + player.getPlayerId(),
                    String.valueOf(System.currentTimeMillis()));
            jedis.expire(RedisOper.TEAM_TIME_PREFIX + player.getPlayerId(), timeout);
        }

        CreateTeamHandlerParam param = new CreateTeamHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setName(player.getPlayerName());
        param.setLevel(player.getLevel());
        int gameServerId = ServerConfig.getInstance().getServerId();
        param.setGameServerId(gameServerId);
        param.setDungeonCid(dungeonCid);
        param.setHeroCid(player.getHeroManager().getHelpFightHeroCid());
        param.setSkinCid(player.getSkinCid());
        /*
         * try { G2FCreateTeamProducerService.getDefault().createTeam(param); } catch (Exception e)
         * { LOGGER.error("创建队伍，kafka出错" + ExceptionEx.e2s(e)); }
         */
        
        // 把申请信息放入到redis中 记得在战斗服取出来时要删除
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.set(RedisOper.TEAM_LEADER_PREFIX + player.getPlayerId(),
                    JSONObject.toJSONString(param));
            jedis.expire(RedisOper.TEAM_LEADER_PREFIX + player.getPlayerId(), timeout + 60);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        //创建队伍写入redis
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.zadd, RedisOper.TEAM_CREATE_PREFIX,
                Double.valueOf(String.valueOf(new Date().getTime())),
                String.valueOf(player.getPlayerId()));
        
        MessageUtils.returnEmptyBody();
    }

    @Override
    public void reqMatchTeam(Player player, int dungeonCid, int teamType) {
        if (!TeamService.getDefault().canApplyJoinTeam(player.getPlayerId())) {
            long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
            if (teamId != 0) {
                TeamService.getDefault().sendCreateTeamInfoMsg(player, Long.valueOf(teamId));
            } else {
                // 返回客户端已经在匹配中
                MessageUtils.throwCondtionError(GameErrorCode.ALREADY_EXIST_MATCH,
                        player.getPlayerId() + "已存在匹配中");
            }
            return;    
        }
        
        
        TeamService.getDefault().checkBeforeCreate(player, teamType, dungeonCid);
        // 记录队伍操作时间 避免短期内重复请求
        DiscreteDataCfgBean timeoutCfgBean = GameDataManager.getDiscreteDataCfgBean(17001);
        int timeout = ToolMap.getInt("time", timeoutCfgBean.getData(), 300);
        TeamRedisService.setPlayerTeamTime(player.getPlayerId());
        TeamRedisService.setExpireTime(RedisOper.TEAM_TIME_PREFIX + player.getPlayerId(), timeout);
        
        MatchTeamHandlerParam param = new MatchTeamHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setName(player.getPlayerName());
        param.setLevel(player.getLevel());
        int gameServerId = ServerConfig.getInstance().getServerId();
        param.setGameServerId(gameServerId);
        param.setHeroCid(player.getHeroManager().getHelpFightHeroCid());
        param.setSkinCid(player.getSkinCid());
        param.setDungeonCid(dungeonCid);
        // 把申请信息放入到redis中 记得在战斗服取出来时要删除
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.set(RedisOper.TEAM_MATCH_PREFIX + player.getPlayerId(),
                    JSONObject.toJSONString(param));
            jedis.expire(RedisOper.TEAM_MATCH_PREFIX + player.getPlayerId(),
                    timeout + 60);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return;
        }
        // 如果是单人匹配 则写入redis
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.zadd, RedisOper.TEAM_DUNGEON_PREFIX + dungeonCid,
                Double.valueOf(String.valueOf(new Date().getTime())),
                String.valueOf(player.getPlayerId()));
        MessageUtils.returnEmptyBody();
    }

    @Override
    public void invitTeam(Player player, int targetPlayerId, String content) {
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0) {
            // 返回客户端没有队伍
            SMessage msg = new SMessage(RespChangeHero.MsgID.eMsgID_VALUE,
                    GameErrorCode.NOT_YET_JOIN_TEAM);
            MessageUtils.send(player.getCtx(), msg);
            return;
        }
        InviteTeamHandlerParam param = new InviteTeamHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setTargetPlayerId(targetPlayerId);
        param.setContent(content);
        param.setSenderName(player.getPlayerName());
        param.setSenderLevel(player.getLevel());
        param.setSenderHeroCid(player.getHeroManager().getHelpFightHeroCid());

        int serverId = LocationServerUtil.getServerId(targetPlayerId);
        if (serverId <= 0)
            return;
        try {
            G2GInviteTeamProductService.getDefault().invite(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void reqJoinTeam(Player player, C2STeamMsg.ReqJoinTeam msg) {
        if (!TeamService.getDefault().canApplyJoinTeam(player.getPlayerId())) {
            long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
            if (teamId != 0) {
                TeamService.getDefault().sendCreateTeamInfoMsg(player, Long.valueOf(teamId));
            } else {
                // 正在加入队伍中
                MessageUtils.throwCondtionError(GameErrorCode.ALREADY_EXIST_TEAM,
                        player.getPlayerId() + "已存在队伍");
            }
            return;    
        }
        
        long teamId = Long.valueOf(msg.getTeamId());
        TeamInfo teamInfo = LogicScriptsUtils.getTeamScript().getTeamInfo(teamId);
        if (teamInfo == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_TEAM, teamId + "队伍没找到");
            return;
        }
        
        if (teamInfo.getStatus() == ETeamStatus.FIGHTING) {
            MessageUtils.throwCondtionError(GameErrorCode.TEAM_RUNING, teamId + "队伍已经战斗中");
            return;
        }
        TeamService.getDefault().checkBeforeCreate(player, teamInfo.getTeamType().getType(),
                teamInfo.getDungeonCid());

        TeamRedisService.setPlayerTeamTime(player.getPlayerId());
        TeamRedisService.setExpireTime(RedisOper.TEAM_TIME_PREFIX + player.getPlayerId(), 1800);

        AcceptTeamHandlerParam param = new AcceptTeamHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setTeamId(teamId);
        param.setName(player.getPlayerName());
        param.setLevel(player.getLevel());
        int gameServerId = ServerConfig.getInstance().getServerId();
        param.setGameServerId(gameServerId);
        param.setHeroCid(player.getHeroManager().getHelpFightHeroCid());
        param.setSkinCid(player.getSkinCid());
        try {
            G2FProductService.getDefault().sendToFightServer(teamInfo.getServerId(), param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void changeHero(int playerId, long teamId, int heroCid, int skinCid) {
        ChangeHeroParam param = new ChangeHeroParam();
        param.setPlayerId(playerId);
        param.setHeroCid(heroCid);
        param.setTeamId(teamId);
        param.setSkinCid(skinCid);
        // 这里要取的是哪个战斗服
        int serverId = TeamRedisService.getTeamServerId(teamId);
        if (serverId <= 0)
            return;
        try {
            G2FProductService.getDefault().sendToFightServer(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void changeStatus(Player player, C2STeamMsg.ReqChangeTeamStatus msg) {
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0) {
            // 返回客户端没有队伍
            MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                    player.getPlayerId() + "没有队伍");
            return;
        }
        int status = msg.getStatus();
        ChangeStatusHandlerParam param = new ChangeStatusHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setStatus(status);
        param.setTeamId(teamId);
        int serverId = TeamRedisService.getTeamServerId(teamId);
        if (serverId <= 0) {
            return;
        }
        try {
            G2FProductService.getDefault().sendToFightServer(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        MessageUtils.returnEmptyBody();
    }

    @Override
    public void changeMemberStatus(int playerId, long teamId, int status) {
        ChangeMemberStatusHandlerParam param = new ChangeMemberStatusHandlerParam();
        param.setPlayerId(playerId);
        param.setStatus(status);
        param.setTeamId(teamId);
        int serverId = TeamRedisService.getTeamServerId(teamId);
        if (serverId <= 0)
            return;
        try {
            G2FProductService.getDefault().sendToFightServer(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void treatMember(Player player, C2STeamMsg.ReqTreatMember msg) {

        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0) {
            // 返回客户端没有队伍
            MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                    player.getPlayerId() + "没有队伍");
            return;
        }

        int targetPid = msg.getTargetPid();
        int type = msg.getType();

        TreatMemberHandlerParam param = new TreatMemberHandlerParam();
        param.setPlayerId(player.getPlayerId());
        param.setTargetPid(targetPid);
        param.setTeamId(teamId);
        param.setType(type);
        int serverId = TeamRedisService.getTeamServerId(teamId);
        if (serverId <= 0)
            return;
        try {
            G2FProductService.getDefault().sendToFightServer(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        MessageUtils.returnEmptyBody();
    }

    @Override
    public void exitTeam(int playerId, long teamId) {
        ExitTeamHandlerParam param = new ExitTeamHandlerParam();
        param.setPlayerId(playerId);
        param.setTeamId(teamId);
        int serverId = TeamRedisService.getTeamServerId(teamId);
        try {
            if (serverId != 0)
                G2FProductService.getDefault().sendToFightServer(serverId, param);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void createPlayerInitialize(Map<Integer, ChasmInfo> chasmInfos) {
        List<ChasmDungeonCfgBean> cfgBeans = GameDataManager.getChasmDungeonCfgBeans();
        for (ChasmDungeonCfgBean cfgBean : cfgBeans) {
            if (!chasmInfos.containsKey(cfgBean.getId())) {
                ChasmInfo chasmInfo = new ChasmInfo();
                chasmInfo.create(cfgBean.getId());
                chasmInfos.put(chasmInfo.getId(), chasmInfo);
            }
        }
    }

    @Override
    public void tick(Player player, Map<Integer, ChasmInfo> chasmInfos) {
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        long currentTime = System.currentTimeMillis();
        if (currentTime >= teamDungeonManager.getAcrossDayTime()) {
            for (Map.Entry<Integer, ChasmInfo> entry : chasmInfos.entrySet()) {
                ChasmInfo chasmInfo = entry.getValue();
                chasmInfo.setFightCount(0);
            }
            RsepEnterChasm.Builder enterChasmBuilder = RsepEnterChasm.newBuilder();
            enterChasmBuilder.addAllChashs(buildChasmInfos(chasmInfos));
            MessageUtils.send(player, enterChasmBuilder);
            teamDungeonManager.setAcrossDayTime(getRefreshTime());
        }
    }

    @Override
    public long getRefreshTime() {
        DiscreteDataCfgBean cfgBean = GameDataManager.getDiscreteDataCfgBean(18001);
        int minute = 300;
        if (cfgBean != null) {
            minute = ToolMap.getInt("refreshTime", cfgBean.getData(), 300);
        }

        Date date = DateEx.getNextDateMinute(new Date(), minute);
        return date.getTime();
    }

    public List<S2CChasmMsg.ChasmInfo> buildChasmInfos(Map<Integer, ChasmInfo> chasmInfos) {
        List<S2CChasmMsg.ChasmInfo> chasmInfoBuilds = new ArrayList<>();
        for (Map.Entry<Integer, ChasmInfo> entry : chasmInfos.entrySet()) {
            ChasmInfo chasmInfo = entry.getValue();
            chasmInfoBuilds.add(chasmInfo.buildChasmInfo());
        }
        return chasmInfoBuilds;
    }

    @Override
    public void reqCancelMatch(Player player) {
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId != 0) {
            MessageUtils.throwCondtionError(GameErrorCode.ALREADY_EXIST_TEAM,
                    player.getPlayerId() + "已经匹配到队伍");
            return;
        }
        if (TeamService.getDefault().canApplyJoinTeam(player.getPlayerId())) {
            RespCancelMatch.Builder builder =
                    RespCancelMatch.newBuilder().setType(TeamCancelMatchType.CANCEL.getType());
            MessageUtils.send(player, builder);
            TeamRedisService.delPlayerTeamTime(player.getPlayerId());
            return;
        }
        boolean isSuccess = cancelMatchTeam(player.getPlayerId());
        if (isSuccess) {
            TeamRedisService.delPlayerTeamTime(player.getPlayerId());
        }
        RespCancelMatch.Builder builder = RespCancelMatch.newBuilder().setType((isSuccess)
                ? TeamCancelMatchType.CANCEL.getType() : TeamCancelMatchType.CANCEL_FAIL.getType());
        MessageUtils.send(player, builder);
    }

    private boolean cancelMatchTeam(int playerId) {
        Long num = TeamRedisService.delPlayerMatch(playerId);
        boolean isSuccess = num > 0L;
        return isSuccess;
    }

    // kafka处理脚本
    public void createTeamF2G(F2GCreateTeamResultParam json) {
        int playerId = json.getPlayerId();
        long teamId = json.getTeamId();
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player != null && player.isOnline()) {
            PlayerProcessorManager.getInstance().addPlayerHandler(player,
                    new LCreateTeamHandler(player, teamId));
        } else {
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
        }
    }

    @Override
    public void treatMemberF2G(F2GTreatMemberHandlerParam json) {
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        if (player == null || !player.isOnline())
            return;
        if (json.getType() == TeamTreatType.KICK.getType()) {
            // 如果是本人被踢则发送5893 如果是其他人被踢则发送5898
            if (json.getPlayerId() == json.getOperatedPid()) {
                RespLeaveTeam.Builder builder =
                        RespLeaveTeam.newBuilder().setType(TeamLeaveType.KICK.getType());
                MessageUtils.send(player, builder);
                try (Jedis jedis =
                        RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
                    jedis.del(RedisOper.TEAM_ID_PREFIX + json.getPlayerId());
                    jedis.del(RedisOper.TEAM_TIME_PREFIX + json.getPlayerId());
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            } else {
                TeamService.getDefault().sendTeamInfoMsg(player, json.getTeamId());
            }
        } else if (json.getType() == TeamTreatType.PROMOTE.getType()) {
            // 所有人发送5898
            TeamService.getDefault().sendTeamInfoMsg(player, json.getTeamId());
        }
    }

    @Override
    public void acceptTeamF2G(F2GAcceptTeamHandlerParam json) {
        // 回给除加入队伍的人5898 回加入队伍的人5894
        if (json.isEntrant()) {
            TeamRedisService.setPlayerTeamId(json.getPlayerId(), json.getTeamId());
        }
        TeamInfo teamInfo = TeamService.getDefault().getTeamInfo(json.getTeamId());
        S2CTeamMsg.TeamInfo.Builder teamBuilder = TeamService.getDefault().makeMsg(teamInfo);
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        if (player == null || !player.isOnline())
            return;
        if (json.isEntrant()) {
            RespJoinTeam.Builder builder = RespJoinTeam.newBuilder();
            builder.setTeam(teamBuilder);
            MessageUtils.send(player, builder);
        } else {
            MessageUtils.send(player, teamBuilder);
        }
    }

    @Override
    public void reqChasmStartFight(Player player) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            String teamIdStr = jedis.get(RedisOper.TEAM_ID_PREFIX + player.getPlayerId());
            if (teamIdStr == null) {
                // 还没有加入队伍
                MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                        player.getPlayerId() + "还没有队伍");
                return;
            }

            long teamId = Long.parseLong(teamIdStr);
            String serverIdStr = jedis.hget(RedisOper.TEAM_PREFIX + teamId, "serverId");
            if (serverIdStr == null || serverIdStr.equals("")) {
                MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                        player.getPlayerId() + "还没有队伍");
                return;
            }

            int serverId = Integer.parseInt(serverIdStr);
            ReqChasmStartFightParam param = new ReqChasmStartFightParam();
            param.setPlayerId(player.getPlayerId());
            param.setTeamId(teamId);
            try {
                if (serverId != 0)
                    G2FProductService.getDefault().sendToFightServer(serverId, param);
            } catch (Exception e) {
                LOGGER.error("开始战斗错误" + ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    public void startChasmFightF2G(ResChasmStartFightParam param) {
        long teamId = param.getTeamId();
        Map<String, String> teamInfoMap = null;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            teamInfoMap = jedis.hgetAll(RedisOper.TEAM_PREFIX + teamId);
        }

        if (teamInfoMap != null && teamInfoMap.size() > 0) {
            TeamInfo teamInfo = new TeamInfo(teamInfoMap);
            Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();

            ResultCode resultCode = ResultCode.getResultCode(param.getResultCode());
            if (resultCode == ResultCode.SUCESS) {
                try {
                    RsepChasmStartFight.Builder respChasmStartFightBuilder =
                            RsepChasmStartFight.newBuilder();
                    respChasmStartFightBuilder.setFightId(String.valueOf(teamInfo.getTeamId()));
                    respChasmStartFightBuilder.setFightServerHost(teamInfo.getServerIp());
                    respChasmStartFightBuilder.setFightServerPort(teamInfo.getServerPort());
                    respChasmStartFightBuilder.setRandomSeed(teamInfo.getRandomSeed());
                    respChasmStartFightBuilder.setDungeonCid(teamInfo.getDungeonCid());
                    respChasmStartFightBuilder.setNetType(param.getNetType());
                    Player player = null;
                    for (Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
                        TeamMember teamMember = entry.getValue();
                        if (teamMember.getServerId() != ServerConfig.getInstance().getServerId()) {
                            player = PlayerViewService.getInstance()
                                    .getPlayerView(teamMember.getPid());
                        } else {
                            player = PlayerManager.getPlayerByPlayerId(teamMember.getPid());
                        }
                        FightPlayer.Builder fightPlayerBuilder = FightPlayer.newBuilder();
                        fightPlayerBuilder.setPid(teamMember.getPid());
                        fightPlayerBuilder.setPname(teamMember.getName());
                        Hero hero = player.getHeroManager().getHero(teamMember.getHeroCid());
                        fightPlayerBuilder.addHeros(hero.buildHeroInfo(ChangeType.DEFAULT));
                        fightPlayerBuilder.setReviveCount(teamMember.getReliveCount());
                        respChasmStartFightBuilder.addPlayers(fightPlayerBuilder);
                    }
                    for (Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
                        TeamMember teamMember = entry.getValue();
                        if (teamMember.getServerId() == ServerConfig.getInstance().getServerId()) {
                            player = PlayerManager.getPlayerByPlayerId(teamMember.getPid());
                            if (player != null && player.isOnline()) {
                                MessageUtils.send(player, respChasmStartFightBuilder);
                            }
                        }
                    }
                    
                    try {
                        LogProcessor.getInstance().sendLog(LogBeanFactory.createBattleLog(player, teamInfo.getDungeonCid(), 0, JSON.toJSONString(teamInfo.toHash()), EReason.CHASM_START.value(), String.valueOf(teamInfo.getTeamId())));
                    } catch (Exception e) {
                        LOGGER.error(ExceptionEx.e2s(e));
                    }
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            } else {
                for (Map.Entry<Integer, TeamMember> entry : teamMembers.entrySet()) {
                    TeamMember teamMember = entry.getValue();
                    if (teamMember.getServerId() == ServerConfig.getInstance().getServerId()) {
                        Player player = PlayerManager.getPlayerByPlayerId(teamMember.getPid());
                        if (player != null && player.isOnline()) {
                            if (resultCode == ResultCode.REPEAT_HERO) {
                                // 重复的精灵
                                SMessage msg = new SMessage(
                                        S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE,
                                        2100074);
                                MessageUtils.send(player.getCtx(), msg);
                            } else if (resultCode == ResultCode.NOT_READY) {
                                SMessage msg = new SMessage(
                                        S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE,
                                        GameErrorCode.NOT_ALL_READY);
                                MessageUtils.send(player.getCtx(), msg);
                            } else if (resultCode == ResultCode.PERMISSION_DENIED) {
                                SMessage msg = new SMessage(
                                        S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE,
                                        GameErrorCode.PERMISSION_DENIED);
                                MessageUtils.send(player.getCtx(), msg);
                            } else if(resultCode == ResultCode.ROOM_LACKING) {
                                SMessage msg = new SMessage(
                                        S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE,
                                        GameErrorCode.ROOM_LACKING);
                                MessageUtils.send(player.getCtx(), msg);
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqChasmFightRevive(Player player) {
        Map<String, String> teamInfoMap = null;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            String teamIdStr = jedis.get(RedisOper.TEAM_ID_PREFIX + player.getPlayerId());
            if (teamIdStr == null) {
                // 还没有加入队伍
                MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                        player.getPlayerId() + "还没有队伍");
                return;
            }

            teamInfoMap = jedis.hgetAll(RedisOper.TEAM_PREFIX + teamIdStr);
        }

        if (teamInfoMap != null && teamInfoMap.size() != 0) {
            TeamInfo teamInfo = new TeamInfo(teamInfoMap);
            ChasmDungeonCfgBean cfgBean =
                    GameDataManager.getChasmDungeonCfgBean(teamInfo.getDungeonCid());
            List<Map<Integer, Integer>> reviveCost = cfgBean.getReviveCost();
            Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
            TeamMember teamMember = teamMembers.get(player.getPlayerId());
            if (teamMember.getReliveCount() >= reviveCost.size()) {
                // 复活次数已用完
                MessageUtils.throwCondtionError(GameErrorCode.REVIVE_COUNT_EXHAUST, "复活次数已用完");
                return;
            }
            Map<Integer, Integer> costMap = reviveCost.get(teamMember.getReliveCount());
            BagManager bagManager = player.getBagManager();
            boolean isEnough = bagManager.enoughByTemplateId(costMap);
            if (!isEnough) {
                MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "请求复活，消耗道具不足");
                return;
            }
            try {
                ReqChasmFightReviveParam param = new ReqChasmFightReviveParam();
                param.setTeamId(teamInfo.getTeamId());
                param.setPid(player.getPlayerId());
                if (teamInfo.getServerId() != 0)
                    G2FProductService.getDefault().sendToFightServer(teamInfo.getServerId(), param);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void chasmFightReviveF2G(F2GChasmFightReviveParam param) {
        int playerId = param.getPlayerId();
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player != null && player.isOnline()) {
            if (param.isSuccess()) {
                ChasmDungeonCfgBean cfgBean =
                        GameDataManager.getChasmDungeonCfgBean(param.getDungeonCid());
                List<Map<Integer, Integer>> reviveCost = cfgBean.getReviveCost();
                Map<Integer, Integer> costMap = reviveCost.get(param.getReliveCount());
                boolean isCostItem = false;
                for(Map.Entry<Integer, Integer> entry : costMap.entrySet()) {
                    if(entry.getValue() != 0) {
                        isCostItem = true;
                    }
                    break;
                }
                if(isCostItem) {
                    BagManager bagManager = player.getBagManager();
                    boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(costMap, true,
                            EReason.CHASM_RELIVE);
                    if (!isEnough) {
                        LOGGER.error(playerId + "请求复活道具不足！");
                    }
                }
            }
            ResqChasmFightRevive.Builder builder = ResqChasmFightRevive.newBuilder();
            builder.setIsSuccess(param.isSuccess());
            MessageUtils.send(player, builder);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endFightOnline(Player player, F2GEndFightParam param) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.del(RedisOper.TEAM_TIME_PREFIX + player.getPlayerId());
            jedis.del(RedisOper.TEAM_ID_PREFIX + player.getPlayerId());
        }
        
        RespEndFight.Builder respEndFightBuilder = RespEndFight.newBuilder();
        respEndFightBuilder.setWin(param.isWin());
        respEndFightBuilder.setFightTime(param.getFightTime());
        if (param.isWin()) {
            List<MemberData> memberDatas = param.getMemberDatas();
            for (MemberData memberData : memberDatas) {
                FightResultdetails.Builder fightResultdetailsBuilder = FightResultdetails.newBuilder();
                fightResultdetailsBuilder.setPid(memberData.getPid());
                fightResultdetailsBuilder.setHurt(memberData.getHurt());
                if (memberData.isMvp()) {
                    fightResultdetailsBuilder.setMvp(true);
                } else {
                    fightResultdetailsBuilder.setMvp(false);
                }
                respEndFightBuilder.addResults(fightResultdetailsBuilder);
            }
            
            Map<Integer, Integer> awardItems = new HashMap<>();
            ChasmDungeonCfgBean chasmDungeonCfgBean = GameDataManager
                    .getChasmDungeonCfgBean(param.getDungeonCid());
            for (MemberData memberData : memberDatas) {
                if(memberData.getPid() == param.getPlayerId()) {
                    if (memberData.isMvp()) {
                        BaseGoods item = GameDataManager.getBaseGoods(chasmDungeonCfgBean.getReward()[1]);
                        ItemPackageHelper.unpack(item.getUseProfit(), null, awardItems);
                    } else {
                        BaseGoods item = GameDataManager.getBaseGoods(chasmDungeonCfgBean.getReward()[0]);
                        ItemPackageHelper.unpack(item.getUseProfit(), null, awardItems);
                        
                    }
                    for (Entry<Integer, Integer> e : awardItems.entrySet()) {
                        RewardsMsg.Builder rewards = RewardsMsg.newBuilder();
                        rewards.setId(e.getKey());
                        rewards.setNum(e.getValue());
                        respEndFightBuilder.addRewards(rewards);
                    }
                    break;
                }
            }     
            
            Map<Integer, Integer> costMap = chasmDungeonCfgBean.getFightCost();
            BagManager bagManager = player.getBagManager();
            boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(costMap, true,
                    EReason.CHASM_THROUGH);
            if (!isEnough) {
                LOGGER.error("深渊关卡" + param.getDungeonCid() + "消耗道具不足");
            }
            TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
            ChasmInfo chasmInfo = teamDungeonManager.getChasmInfo(param.getDungeonCid());
            chasmInfo.setFightCount(chasmInfo.getFightCount() + 1);

            bagManager.addItems(awardItems, true, EReason.CHASM_THROUGH);

            RsepEnterChasm.Builder chasmBuilder = RsepEnterChasm.newBuilder();
            chasmBuilder.addAllChashs(teamDungeonManager.buildChasmInfos());
            MessageUtils.send(player, chasmBuilder);
        }
        
        MessageUtils.send(player, respEndFightBuilder);
    }

    @Override
    public void endFightOffline(int playerId, F2GEndFightParam param) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.del(RedisOper.TEAM_TIME_PREFIX + playerId);
            jedis.del(RedisOper.TEAM_ID_PREFIX + playerId);
        }
    }

    @Override
    public void dissolveTeam(Player player) {
        TeamRedisService.delPlayerTeamId(player.getPlayerId());
        TeamRedisService.delPlayerTeamTime(player.getPlayerId());
        RespLeaveTeam.Builder builder =
                RespLeaveTeam.newBuilder().setType(TeamLeaveType.TIME_OUT.getType());
        MessageUtils.send(player, builder);    
    }

}
