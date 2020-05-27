package logic.mail.handler;

import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SMailMsg;
import org.game.protobuf.c2s.C2SMailMsg.MailHandleMsg;

@MHandler(messageClazz = C2SMailMsg.MailHandleMsg.class)
public class MReqMailHandleHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        MailHandleMsg msg = (MailHandleMsg) getMessage().getData();
        LogicScriptsUtils.getMailScript().handleMails(player, msg);
    }
}
