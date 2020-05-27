package logic.buyResources.handler;

import logic.character.bean.Player;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SPlayerMsg.ReqBuyResources;

import exception.AbstractLogicModelException;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SPlayerMsg.ReqBuyResources.class)
public class MReqBuyResourcesHandler extends MessageHandler {
	@Override
	public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        ReqBuyResources msg = (ReqBuyResources) getMessage().getData();
        player.getBuyResourcesManager().reqBuyResources(msg.getCid(), msg.getNum());
	}
}