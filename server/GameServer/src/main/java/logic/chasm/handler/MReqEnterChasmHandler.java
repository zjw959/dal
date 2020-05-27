package logic.chasm.handler;

import org.game.protobuf.c2s.C2SChasmMsg;
import org.game.protobuf.s2c.S2CChasmMsg.RsepEnterChasm;

import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SChasmMsg.ReqEnterChasm.class)
public class MReqEnterChasmHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.TEAM_FIGHT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:TEAM_FIGHT");
        }
        Player player = (Player) getGameData();
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        
        RsepEnterChasm.Builder enterChasmBuilder = RsepEnterChasm.newBuilder();
        enterChasmBuilder.addAllChashs(teamDungeonManager.buildChasmInfos());
        MessageUtils.send(player, enterChasmBuilder);
    }

}
