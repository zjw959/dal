package logic.summon.handler;

import org.game.protobuf.c2s.C2SSummonMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.summon.SummonManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SSummonMsg.GetComposeInfo.class)
public class MReqGetComposeInfoHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        C2SSummonMsg.GetComposeInfo msg = (C2SSummonMsg.GetComposeInfo) getMessage().getData();
        SummonManager manager = player.getSummonManager();
        manager.reqGetComposeInfo(msg);
    }

}
