package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.FriendHandlerParam;
import logic.friend.FriendManager;

public class G2GFriendHandler implements TeamActionHandler<FriendHandlerParam> {

    @Override
    public void process(FriendHandlerParam json) {
        FriendManager.process(json.getNotifyList());
    }

}
