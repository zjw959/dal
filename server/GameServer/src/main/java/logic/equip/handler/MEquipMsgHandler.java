package logic.equip.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import logic.equip.EquipService;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEquipmentMsg.EquipMsg;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SEquipmentMsg.EquipMsg.class)
public class MEquipMsgHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MEquipMsgHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		org.game.protobuf.c2s.C2SEquipmentMsg.EquipMsg msg = (EquipMsg) getMessage().getData();
		
		EquipService.getInstance().equip(player, msg.getHeroId(), msg.getEquipmentId(), msg.getPosition());
		
	}
}