package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqTreatMember.class)
public class MReqTreatMemberHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2STeamMsg.ReqTreatMember msg = (C2STeamMsg.ReqTreatMember) getMessage().getData();
        player.getTeamDungeonManager().treatMember(msg);
    }

}
