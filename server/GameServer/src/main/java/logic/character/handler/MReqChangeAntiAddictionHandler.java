package logic.character.handler;

import logic.character.bean.Player;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.ResChangeAntiAddiction;

@MHandler(messageClazz = C2SPlayerMsg.ReqChangeAntiAddiction.class)
public class MReqChangeAntiAddictionHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        C2SPlayerMsg.ReqChangeAntiAddiction msg =
                (C2SPlayerMsg.ReqChangeAntiAddiction) getMessage().getData();
        int anti = msg.getAnti();
        player.getInfoManager().setAntiStatus(anti);
        ResChangeAntiAddiction.Builder builder = ResChangeAntiAddiction.newBuilder();
        builder.setAnti(anti);
        MessageUtils.send(player, builder);
    }
}
