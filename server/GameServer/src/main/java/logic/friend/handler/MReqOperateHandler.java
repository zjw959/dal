package logic.friend.handler;

import logic.character.bean.Player;
import logic.friend.FriendManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.RespOperate;

@MHandler(messageClazz = C2SFriendMsg.ReqOperate.class)
public class MReqOperateHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        C2SFriendMsg.ReqOperate msg = (C2SFriendMsg.ReqOperate) getMessage().getData();
        FriendManager manager = player.getFriendManager();
        manager.operate(msg);
        RespOperate.Builder builder = RespOperate.newBuilder();
        builder.addAllTargets(msg.getTargetsList()).setType(msg.getType()).build();
        MessageUtils.send(player, builder);
    }
}
