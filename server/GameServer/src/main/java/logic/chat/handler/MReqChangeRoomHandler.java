package logic.chat.handler;

import org.game.protobuf.c2s.C2SChatMsg;

import exception.AbstractLogicModelException;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chat.ChatService;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SChatMsg.ReqChangeRoom.class)
public class MReqChangeRoomHandler extends MessageHandler {
	
	@Override
    public void action() throws AbstractLogicModelException {
		Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        C2SChatMsg.ReqChangeRoom msg = (C2SChatMsg.ReqChangeRoom) getMessage().getData();
		if(msg.hasRoomId()){
            ChatService.getInstance().enterChatRoom(player, msg.getRoomId(), false);
		}else{
            ChatService.getInstance().enterChatRoom(player, -1, false);
		}
		
	}
}
