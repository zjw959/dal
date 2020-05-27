package logic.chat.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import exception.AbstractLogicModelException;
import kafka.service.ChatRoomProducerService;
import kafka.team.param.g2g.InviteTeamSystemHandlerParam;
import kafka.team.param.g2g.PublicChatHandlerParam;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.chat.ChatChannelType;
import logic.chat.ChatFunctionType;
import logic.chat.ChatService;
import logic.constant.GameErrorCode;
import logic.login.struct.ChannelInfo;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;
import message.SMessage;
import net.AttributeKeys;
import thread.base.LBaseHandler;
import thread.player.PlayerProcessorManager;
import utils.SensitiveWordsUtil;

/**
 * 独立的多线程运行
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SChatMsg.ChatMsg.class)
public class MReqSendChatHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(ChatService.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        boolean state = channelInfo.isKeepSilent();
        if (state) {
            // 被禁言
            MessageUtils.throwCondtionError(GameErrorCode.INTERCEPT_CHAT,
                    "你已经被禁言:" + player.getPlayerId());
            return;
        }
        C2SChatMsg.ChatMsg msg = (C2SChatMsg.ChatMsg) getMessage().getData();
        String content = msg.getContent();
        // 先检查是否是gm指令
        boolean isGmCommond = ChatService.getInstance().isGM(content);
        if (isGmCommond) {
            PlayerProcessorManager.getInstance().addPlayerHandler(player,
                    new LGMInnerHandler(player, content));
        } else {
            ChatService.getInstance().getProcess()
                    .executeInnerHandler(new LChatInnerHandler(player, content, msg));
        }
    }

    private class LGMInnerHandler extends LBaseHandler {
        String content;

        public LGMInnerHandler(Player player, String content) {
            super(player);
            this.content = content;
        }

        @Override
        public void action() throws Exception {
            ChatService.getInstance().executeGmCommand(player, content);
            MessageUtils.send(player, S2CChatMsg.RespGMCallBack.newBuilder());
            SMessage msg = new SMessage(S2CChatMsg.ChatMsg.MsgID.eMsgID_VALUE, 0);
            MessageUtils.send(ctx, msg);
        }
    }

    private class LChatInnerHandler extends LBaseHandler {
        String content;
        C2SChatMsg.ChatMsg msg;

        public LChatInnerHandler(Player player, String content, C2SChatMsg.ChatMsg msg) {
            super(player);
            this.content = content;
            this.msg = msg;
        }

        @Override
        public void action() throws Exception {
            if (msg.getChannel() == ChatChannelType.PRIVATE) {
                if (msg.getFun() == ChatFunctionType.CHAT) {
                    content = SensitiveWordsUtil.filterAndReplace(content);
                    ChatService.getInstance().sendPrivateMsg(player, msg.getPlayerId(), content);
                } else if (msg.getFun() == ChatFunctionType.CHASM) {
                    TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
                    teamDungeonManager.invitTeam(msg.getPlayerId(), content);
                }
            } else if (msg.getChannel() == ChatChannelType.PUBLIC) {
                int roomId = ChatService.getInstance().getRoomId(player);
                if (msg.getFun() == ChatFunctionType.CHAT) {
                    content = SensitiveWordsUtil.filterAndReplace(content);
                    PublicChatHandlerParam chatObject = new PublicChatHandlerParam(
                            player.getPlayerId(), player.getPlayerName(), player.getLevel(),
                            player.getHeroManager().getHelpFightHeroCid(), content, roomId);
                    try {
                        ChatRoomProducerService.getDefault().sendChatRoom(chatObject);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error(e);
                    }
                } else if (msg.getFun() == ChatFunctionType.CHASM) {
                    InviteTeamSystemHandlerParam chatObject = new InviteTeamSystemHandlerParam(
                            player.getPlayerId(), player.getPlayerName(), player.getLevel(),
                            player.getHeroManager().getHelpFightHeroCid(), content, roomId);
                    try {
                        ChatRoomProducerService.getDefault().sendChatRoom(chatObject);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error(e);
                    }
                }
            } else if (msg.getChannel() == ChatChannelType.TEAM) {
                content = SensitiveWordsUtil.filterAndReplace(content);
                ChatService.getInstance().sendTeamChatMsg(player, content);
            }
            SMessage msg = new SMessage(S2CChatMsg.ChatMsg.MsgID.eMsgID_VALUE, 0);
            MessageUtils.send(ctx, msg);
        }
    }
}
