package logic.chasm.InnerHandler;

import org.game.protobuf.s2c.S2CTeamMsg.RespCreateTeam;
import org.game.protobuf.s2c.S2CTeamMsg.TeamInfo;
import org.game.protobuf.s2c.S2CTeamMsg.TeamMember;

import logic.character.bean.Player;
import logic.chasm.bean.TeamInfo.ETeamStatus;
import logic.chasm.bean.TeamMember.EMemberStatus;
import logic.support.MessageUtils;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import thread.base.LBaseHandler;

public class LCreateTeamHandler extends LBaseHandler {
    private long teamId;
    
    public LCreateTeamHandler(Player player, long teamId) {
        super(player);
        this.teamId = teamId;
    }

    @Override
    public void action() throws Exception {
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            jedis.set(RedisOper.TEAM_ID_PREFIX + player.getPlayerId(), String.valueOf(teamId));
            jedis.expire(RedisOper.TEAM_ID_PREFIX + player.getPlayerId(), 1800);
        }
        TeamMember.Builder memberBuilder = TeamMember.newBuilder();
        memberBuilder.setPid(player.getPlayerId());
        memberBuilder.setStatus(EMemberStatus.IDLE.getStatus());// 队员状态 1:空闲 2:准备中
        memberBuilder.setHeroCid(player.getHeroManager().getHelpFightHeroCid());
        memberBuilder.setName(player.getPlayerName());
        memberBuilder.setPlv(player.getLevel());
        memberBuilder.setSkinCid(player.getSkinCid());
        TeamInfo.Builder teamBuilder = TeamInfo.newBuilder();
        teamBuilder.setTeamId(String.valueOf(teamId));
        teamBuilder.setLeaderPid(player.getPlayerId());
        teamBuilder.setStatus(ETeamStatus.WAITING.getStatus());
        teamBuilder.addMembers(memberBuilder);
        RespCreateTeam.Builder builder = RespCreateTeam.newBuilder();
        builder.setTeam(teamBuilder);
        MessageUtils.send(player, builder);
    }

}
