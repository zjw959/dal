package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqChangeTeamStatus.class)
public class MReqChangeTeamStatusHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2STeamMsg.ReqChangeTeamStatus msg =
                (C2STeamMsg.ReqChangeTeamStatus) getMessage().getData();
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        teamDungeonManager.changeStatus(msg);
    }

}
