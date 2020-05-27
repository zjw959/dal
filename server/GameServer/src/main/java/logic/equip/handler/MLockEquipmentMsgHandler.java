package logic.equip.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEquipmentMsg.LockMsg;

import exception.AbstractLogicModelException;
import logic.equip.EquipService;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SEquipmentMsg.LockMsg.class)
public class MLockEquipmentMsgHandler extends MessageHandler{

	private static final Logger LOGGER = Logger.getLogger(MLockEquipmentMsgHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
			LOGGER.error(this.getClass().getName() + " can not find player ");
			return;
		}
		org.game.protobuf.c2s.C2SEquipmentMsg.LockMsg msg = (LockMsg) getMessage().getData();
		EquipService.getInstance().lockAndUnLock(player, msg.getEquipmentId());
	}

}
