package logic.equip.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import logic.equip.EquipService;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEquipmentMsg.TakeOffEquipmentMsg;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SEquipmentMsg.TakeOffEquipmentMsg.class)
public class MTakeOffEquipmentMsgHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MTakeOffEquipmentMsgHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		
		org.game.protobuf.c2s.C2SEquipmentMsg.TakeOffEquipmentMsg msg =  (TakeOffEquipmentMsg) getMessage().getData();
		EquipService.getInstance().takeOff(player, msg.getHeroId(), msg.getPosition());
	}
}