package logic.favor.handler;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFreshRoleNotice;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResFreshRoleNotice;

import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFreshRoleNotice.class)
public class MReqFreshRoleNoticeHandler extends MessageHandler {

    @Override
    public void action() throws Exception {

        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        ReqFreshRoleNotice msg = (ReqFreshRoleNotice) getMessage().getData();
        player.getFavorDatingManager().dealNoticeStatue(player, msg.getRoleId());
        ResFreshRoleNotice.Builder resp = ResFreshRoleNotice.newBuilder();
        MessageUtils.send(player, resp);
    }
}
