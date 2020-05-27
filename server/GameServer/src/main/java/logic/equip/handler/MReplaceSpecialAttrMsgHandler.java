package logic.equip.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import logic.equip.EquipService;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEquipmentMsg.ReplaceSpecialAttrMsg;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SEquipmentMsg.ReplaceSpecialAttrMsg.class)
public class MReplaceSpecialAttrMsgHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MReplaceSpecialAttrMsgHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		org.game.protobuf.c2s.C2SEquipmentMsg.ReplaceSpecialAttrMsg msg = (ReplaceSpecialAttrMsg) getMessage().getData();
		
		EquipService.getInstance().replaceSpecialAttr(player, msg.getEquipmentId(), msg.getReplace());
	}
}