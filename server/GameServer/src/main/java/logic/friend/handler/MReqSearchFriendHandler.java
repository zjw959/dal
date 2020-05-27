package logic.friend.handler;

import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SFriendMsg;

@MHandler(messageClazz = C2SFriendMsg.ReqQueryPlayer.class)
public class MReqSearchFriendHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
    }
}
