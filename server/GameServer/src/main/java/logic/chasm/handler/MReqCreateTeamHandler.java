package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqCreateTeam;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqCreateTeam.class)
public class MReqCreateTeamHandler extends MessageHandler {
    
    @Override
    public void action() throws Exception {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.TEAM_FIGHT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:TEAM_FIGHT");
        }
        Player player = (Player) getGameData();
        ReqCreateTeam msg = (ReqCreateTeam) getMessage().getData();
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        teamDungeonManager.reqCreateTeam(msg);
    }
}
