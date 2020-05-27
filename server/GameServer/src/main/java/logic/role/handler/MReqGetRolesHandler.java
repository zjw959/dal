package logic.role.handler;

import logic.character.bean.Player;
import logic.msgBuilder.RoleMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfoList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = C2SRoleMsg.GetRole.class)
public class MReqGetRolesHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        RoleInfoList.Builder builder =
                RoleMsgBuilder.createRoleInfoList(ChangeType.DEFAULT, player);
        MessageUtils.send(player, builder);
    }
}
