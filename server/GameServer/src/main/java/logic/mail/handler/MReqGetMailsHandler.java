package logic.mail.handler;

import logic.character.bean.Player;
import logic.mail.MailService;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SMailMsg;

import thread.base.GameBaseProcessor;
import thread.player.PlayerProcessorManager;

@MHandler(messageClazz = C2SMailMsg.GetMails.class)
public class MReqGetMailsHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        GameBaseProcessor proc =
                PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex());
        MailService.getInstance().getProcess().executeInnerHandler(
                new LLoginGetMailsProceHandler(player, proc, new LLoginGetMailsCBHandler(player)));
    }
}
