package logic.chat.handler;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chat.ChatService;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SChatMsg.ReqInitChatInfo.class)
public class MReqInitChatInfoHandler extends MessageHandler {

	@Override
	public void action() throws Exception {
		Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
		// 房到聊天房间里
        ChatService.getInstance().enterChatRoom(player, -1, true);
	}

}
