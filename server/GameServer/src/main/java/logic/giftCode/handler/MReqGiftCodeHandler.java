package logic.giftCode.handler;

import logic.character.bean.Player;
import logic.giftCode.GiftCodeService;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SLoginMsg;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SLoginMsg.GiftCode.class)
public class MReqGiftCodeHandler extends MessageHandler {
    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        C2SLoginMsg.GiftCode msg = (C2SLoginMsg.GiftCode) getMessage().getData();
        // 请求兑换礼包码
        GiftCodeService.getInstance().reqGiftCode(player, msg.getGiftCode());
    }
}
