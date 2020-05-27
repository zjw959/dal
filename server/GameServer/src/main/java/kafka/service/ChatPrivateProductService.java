package kafka.service;

import org.game.protobuf.s2c.S2CChatMsg.ChatInfo;
import kafka.team.action.TeamActionManager;
import kafka.team.param.g2g.PrivateChatHandlerParam;
import kafka.team.param.g2g.TeamChatHandlerParam;
import logic.character.PlayerSnapService;
import logic.character.bean.Player;
import logic.chat.ChatService;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.SMessage;
import server.ServerConfig;
import utils.LocationServerUtil;

public class ChatPrivateProductService extends AbstractProductService{
    private static class DEFAULT {
        private static final ChatPrivateProductService provider = new ChatPrivateProductService();
    }

    public static ChatPrivateProductService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-game.properties";
    }

    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "game" + serverId;
    }

    public void sendChatPrivate(Player player, PrivateChatHandlerParam chatObject)
            throws InstantiationException, IllegalAccessException {
        if (!PlayerSnapService.getPlayerIsOnline(chatObject.getReceiverId())) {
            SMessage msg =
                    new SMessage(ChatInfo.MsgID.eMsgID_VALUE, GameErrorCode.PRIVATE_NOT_ONLINE);
            MessageUtils.send(player.getCtx(), msg);
            return;
        }
        ChatService.getInstance().notifyPrivateMsg(chatObject.getSenderId(), chatObject);
        int serverId = LocationServerUtil.getServerId(chatObject.getReceiverId());
        if (serverId <= 0)
            return;
        if (serverId == ServerConfig.getInstance().getServerId()) {
            ChatService.getInstance().notifyPrivateMsg(chatObject.getReceiverId(), chatObject);
            return;
        }
        String data = TeamActionManager.getDefault().makeParam(chatObject);
        getProducer().send(getTopic(serverId), "", data);
    }

    public void sendChatTeam(Player player, TeamChatHandlerParam chatObject)
            throws InstantiationException, IllegalAccessException {
        if (!PlayerSnapService.getPlayerIsOnline(chatObject.getReceiverId())) {
            return;
        }
        int serverId = LocationServerUtil.getServerId(chatObject.getReceiverId());
        if (serverId <= 0)
            return;
        if (serverId == ServerConfig.getInstance().getServerId()) {
            ChatService.getInstance().notifyTeamMsg(chatObject.getReceiverId(), chatObject);
            return;
        }
        String data = TeamActionManager.getDefault().makeParam(chatObject);
        getProducer().send(getTopic(serverId), "", data);
    }

}
