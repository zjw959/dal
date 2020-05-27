package logic.buyResources.handler;

import java.util.Map;
import java.util.Map.Entry;

import logic.character.bean.Player;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.s2c.S2CPlayerMsg.BuyResourcesLog;
import org.game.protobuf.s2c.S2CPlayerMsg.RespBuyResourcesLog;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SPlayerMsg.ReqBuyResourcesLog.class)
public class MReqBuyResourcesLogHandler extends MessageHandler {
    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        Map<Integer, Integer> buyMap = player.getBuyResourcesManager().getBuyMap();
        RespBuyResourcesLog.Builder resp = RespBuyResourcesLog.newBuilder();
        for (Entry<Integer, Integer> entry : buyMap.entrySet()) {
            BuyResourcesLog.Builder record = BuyResourcesLog.newBuilder();
            record.setCid(entry.getKey());
            record.setCount(entry.getValue());
            record.setCt(ChangeType.DEFAULT);
            resp.addLogs(record);
        }
        MessageUtils.send(player, resp);
    }
}
