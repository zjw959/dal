package thread.player.hanlder;

import org.game.protobuf.s2c.S2CPlayerMsg.RespTargetPlayerInfo;

import logic.character.bean.Player;
import logic.constant.GameErrorCode;
import logic.msgBuilder.PlayerMsgBuilder;
import logic.support.MessageUtils;
import message.SMessage;
import thread.base.LBaseCBHandler;

/**
 * 
 * @Description 获取PlayerView的handler(异步)
 * @author LiuJiang
 * @date 2018年7月2日 下午9:13:01
 *
 */
public class LGetPlayerViewCBHandler extends LBaseCBHandler {
    /** 目标玩家id */
    int targetPlayerId;
    /** 目标玩家信息 */
    Player targetPlayer;

    public LGetPlayerViewCBHandler(Player player, int targetPlayerId) {
        super(player);
        this.targetPlayerId = targetPlayerId;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public int getTargetPlayerId() {
        return targetPlayerId;
    }

    @Override
    public void action() throws Exception {
        Player senderPlayer = player;
        if (senderPlayer == null || !senderPlayer.isOnline()) {
            return;// 玩家已离线
        }
        // 取得玩家信息
        if (targetPlayer == null) {
            SMessage msg = new SMessage(RespTargetPlayerInfo.MsgID.eMsgID_VALUE, new byte[0]);
            msg.setStatus(GameErrorCode.NOR_FONT_PLAYERINFO);
            MessageUtils.send(senderPlayer.getCtx(), msg);
        } else {
            RespTargetPlayerInfo.Builder builder =
                    PlayerMsgBuilder.createPlayerViewBuilder(targetPlayer);
            MessageUtils.send(senderPlayer, builder);
        }
    }
}
