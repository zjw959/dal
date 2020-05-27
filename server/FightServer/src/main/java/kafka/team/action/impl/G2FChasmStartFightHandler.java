package kafka.team.action.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.GameDataManager;
import kafka.service.F2GProductService;
import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ReqChasmStartFightParam;
import kafka.team.param.g2f.ResChasmStartFightParam;
import kafka.team.param.g2f.ResChasmStartFightParam.ResultCode;
import logic.chasm.InnerHandler.LReqFightBeginHandler;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.chasm.bean.TeamInfo.ETeamStatus;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import server.ServerConfig;
import thread.BaseHandler;
import thread.FightRoomPrepareProcessor;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FChasmStartFightHandler implements TeamActionHandler<ReqChasmStartFightParam> {
    @Override
    public void process(ReqChasmStartFightParam param) {
        long teamId = param.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        TeamProcessor teamProcessor = (TeamProcessor) TeamProcessorManager.getInstance()
                .getRoomProcessor(teamInfo.getProcessorId());
        teamProcessor.executeHandler(new BaseHandler() {

            @Override
            public void action() throws Exception {
                if (teamInfo.getStatus() != ETeamStatus.FIGHTING) {
                    ResultCode resultCode = ResultCode.SUCESS;
                    do {
                        // 判断是否有队长权限
                        if (teamInfo.getLeaderId() != param.getPlayerId()) {
                            resultCode = ResultCode.PERMISSION_DENIED;
                            break;
                        }

                        // 判断是否有重复的精灵
                        Map<Integer, TeamMember> teamMembers = teamInfo.getMembers();
                        List<TeamMember> teamMemberList = new ArrayList<>(teamMembers.values());
                        boolean isRepeat = false;
                        Label: for (int i = 0; i < teamMemberList.size(); i++) {
                            TeamMember teamMember1 = teamMemberList.get(i);
                            for (int j = i + 1; j < teamMemberList.size(); j++) {
                                TeamMember teamMember2 = teamMemberList.get(j);
                                int role1 = GameDataManager.getHeroCfgBean(teamMember1.getHeroCid())
                                        .getRole();
                                int role2 = GameDataManager.getHeroCfgBean(teamMember2.getHeroCid())
                                        .getRole();
                                if (role1 == role2) {
                                    isRepeat = true;
                                    break Label;
                                }
                            }
                        }

                        if (isRepeat) {
                            // 精灵重复,不能开始
                            resultCode = ResultCode.REPEAT_HERO;
                            break;
                        }

                        // 判断所有人是否准备就绪
                        if (!teamInfo.allReady()) {
                            resultCode = ResultCode.NOT_READY;
                            break;
                        }
                    } while (false);

                    if (resultCode == ResultCode.SUCESS) {
                        teamInfo.setStatus(ETeamStatus.FIGHTING);
                        Map<String, String> teamInfoMap = null;
                        try (Jedis jedis = RedisServices.getRedisService(ERedisType.Fight.getType())
                                .getJedis()) {
                            jedis.hset(RedisOper.TEAM_PREFIX + teamId, "status",
                                    String.valueOf(ETeamStatus.FIGHTING.getStatus()));
                            teamInfoMap = jedis.hgetAll(RedisOper.TEAM_PREFIX + teamId);
                        }

                        if (teamInfoMap != null && teamInfoMap.size() > 0) {
                            FightRoomPrepareProcessor.getInstance()
                                    .executeHandler(new LReqFightBeginHandler(teamInfoMap));
                        }
                    }
                    
                    ResChasmStartFightParam param = new ResChasmStartFightParam();
                    param.setTeamId(teamInfo.getTeamId());
                    param.setResultCode(resultCode.getCode());
                    param.setNetType(ServerConfig.getInstance().getNetType());
                    Map<Integer, TeamMember> members = teamInfo.getMembers();
                    for (Map.Entry<Integer, TeamMember> entry : members.entrySet()) {
                        TeamMember teamMember = entry.getValue();
                        F2GProductService.getDefault().sendMsg(teamMember.getServerId(), param);
                    }
                }
            }
        });
    }

}
