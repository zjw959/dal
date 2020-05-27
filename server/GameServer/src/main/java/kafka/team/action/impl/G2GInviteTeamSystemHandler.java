package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.InviteTeamSystemHandlerParam;
import logic.chat.ChatService;

public class G2GInviteTeamSystemHandler implements TeamActionHandler<InviteTeamSystemHandlerParam> {

    @Override
    public void process(InviteTeamSystemHandlerParam json) {
        ChatService.getInstance().notifyChatRoom(json);
    }

}
