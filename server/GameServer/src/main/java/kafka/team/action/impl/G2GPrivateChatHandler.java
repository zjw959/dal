package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.PrivateChatHandlerParam;
import logic.chat.ChatService;

public class G2GPrivateChatHandler implements TeamActionHandler<PrivateChatHandlerParam> {

    @Override
    public void process(PrivateChatHandlerParam json) {
        ChatService.getInstance().notifyPrivateMsg(json.getReceiverId(), json);
    }

}
