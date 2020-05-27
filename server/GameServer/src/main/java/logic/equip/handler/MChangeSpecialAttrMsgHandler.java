package logic.equip.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEquipmentMsg;
import org.game.protobuf.c2s.C2SEquipmentMsg.ChangeSpecialAttrMsg;

import exception.AbstractLogicModelException;
import logic.equip.EquipService;
import message.MHandler;
import message.MessageHandler;


@MHandler(messageClazz = org.game.protobuf.c2s.C2SEquipmentMsg.ChangeSpecialAttrMsg.class)
public class MChangeSpecialAttrMsgHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MChangeSpecialAttrMsgHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }

        C2SEquipmentMsg.ChangeSpecialAttrMsg msg = (ChangeSpecialAttrMsg) getMessage().getData();

        EquipService.getInstance().changeSpecialAttr(player, msg.getSourceEquipmentId(),
                msg.getCostEquipmentId(), msg.getOldAttrIndexList(), msg.getCostAttrIndexList());

    }
}
