package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.TeamChatHandlerParam;
import logic.chat.ChatService;

public class G2GTeamChatHandler implements TeamActionHandler<TeamChatHandlerParam> {

    @Override
    public void process(TeamChatHandlerParam json) {
        ChatService.getInstance().notifyTeamMsg(json.getReceiverId(), json);
    }

}
