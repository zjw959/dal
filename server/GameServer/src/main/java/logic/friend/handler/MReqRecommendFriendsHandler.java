package logic.friend.handler;

import org.game.protobuf.c2s.C2SFriendMsg;

import logic.character.PlayerViewService;
import logic.character.bean.Player;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SFriendMsg.ReqRecommendFriends.class)
public class MReqRecommendFriendsHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();

        ((PlayerViewService) PlayerViewService.getInstance()).getRecommands(player,
                this.getMessage().getId(), true);
    }
}
