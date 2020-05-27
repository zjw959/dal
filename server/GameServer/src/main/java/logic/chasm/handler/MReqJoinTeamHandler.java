package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqJoinTeam.class)
public class MReqJoinTeamHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2STeamMsg.ReqJoinTeam msg = (C2STeamMsg.ReqJoinTeam) getMessage().getData();
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
        teamDungeonManager.reqJoinTeam(player, msg);
    }

}
