package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.PublicChatHandlerParam;
import logic.chat.ChatService;

public class G2GPublicChatHandler implements TeamActionHandler<PublicChatHandlerParam> {

    @Override
    public void process(PublicChatHandlerParam json) {
        ChatService.getInstance().notifyChatRoom(json);
    }

}
