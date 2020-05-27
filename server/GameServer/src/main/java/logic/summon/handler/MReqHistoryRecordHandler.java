package logic.summon.handler;

import org.game.protobuf.c2s.C2SSummonMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.summon.SummonManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SSummonMsg.ReqHistoryRecord.class)
public class MReqHistoryRecordHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        C2SSummonMsg.ReqHistoryRecord msg = (C2SSummonMsg.ReqHistoryRecord) getMessage().getData();
        SummonManager manager = player.getSummonManager();
        manager.reqHistoryRecord(msg);
    }

}
