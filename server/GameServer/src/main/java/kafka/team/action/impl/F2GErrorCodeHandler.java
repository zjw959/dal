package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.constants.TeamErrorCodeType;
import kafka.team.param.f2g.F2GErrorCodeHandlerParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.TeamRedisService;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.SMessage;

public class F2GErrorCodeHandler implements TeamActionHandler<F2GErrorCodeHandlerParam> {
    // 只能是加入队伍失败的错误返回，因为这里要删除TeamTime
    @Override
    public void process(F2GErrorCodeHandlerParam json) {
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        if (player == null || !player.isOnline())
            return;
        TeamRedisService.delPlayerTeamTime(player.getPlayerId());
        int errorCode = GameErrorCode.CLIENT_PARAM_IS_ERR;
        if (json.getStatus() == TeamErrorCodeType.TEAM_NOT_EXIST) {
            errorCode = GameErrorCode.NO_TEAM;
        } else if (json.getStatus() == TeamErrorCodeType.FIGHTING) {
            errorCode = GameErrorCode.TEAM_RUNING;
        } else if (json.getStatus() == TeamErrorCodeType.TEAM_FULL) {
            errorCode = GameErrorCode.TEAM_IS_FULL;
        }
        SMessage msg = new SMessage(json.getCode(), errorCode);
        MessageUtils.send(player.getCtx(), msg);
    }

}
