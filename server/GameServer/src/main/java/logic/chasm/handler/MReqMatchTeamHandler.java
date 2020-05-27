package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqMatchTeam.class)
public class MReqMatchTeamHandler extends MessageHandler {
    @Override
    public void action() throws Exception {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.TEAM_FIGHT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:TEAM_FIGHT");
        }
        Player player = (Player) getGameData();
        C2STeamMsg.ReqMatchTeam msg = (C2STeamMsg.ReqMatchTeam) getMessage().getData();
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        teamDungeonManager.reqMatchTeam(msg.getFeature().getDungeonCid(),
                msg.getFeature().getTeamType());
    }
}
