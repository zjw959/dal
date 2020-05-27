package kafka.team.action.impl;

import org.game.protobuf.s2c.S2CChatMsg.ChatInfo;
import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2g.InviteTeamHandlerParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chat.ChatChannelType;
import logic.chat.ChatFunctionType;
import logic.support.MessageUtils;

public class G2GInviteTeamHandler implements TeamActionHandler<InviteTeamHandlerParam> {

    @Override
    public void process(InviteTeamHandlerParam json) {
        Player player = PlayerManager.getPlayerByPlayerId(json.getTargetPlayerId());
        if (player == null || !player.isOnline())
            return;
        ChatInfo.Builder builder = ChatInfo.newBuilder().setChannel(ChatChannelType.PRIVATE)
                .setFun(ChatFunctionType.CHASM)
                .setHelpFightHeroCid(json.getSenderHeroCid())
                .setContent(json.getContent()).setPid(json.getPlayerId())
                .setPname(json.getSenderName()).setLvl(json.getSenderLevel());
        MessageUtils.send(player, builder);
    }

}
