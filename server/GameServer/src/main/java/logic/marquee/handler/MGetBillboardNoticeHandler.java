package logic.marquee.handler;

import logic.character.bean.Player;
/**
 * 获取公告
 */
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.s2c.S2CNoticeMsg.GetBillBoardNotice;

import exception.AbstractLogicModelException;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SNoticeMsg.GetBillboardNotice.class)
public class MGetBillboardNoticeHandler extends MessageHandler {
	@Override
	public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        GetBillBoardNotice.Builder builder = GetBillBoardNotice.newBuilder();
//builder.addAllBillBoardNotice(java.util.Collections.emptyList());
//S2CNoticeMsg.GetBillBoardNotice.Builder builder = S2CNoticeMsg.GetBillBoardNotice.newBuilder();
//noticeList.forEach(notice -> builder.addBillBoardNotice(buildBillBoardNoticeMsg(notice)));
		logic.support.MessageUtils.send(player, builder);
	}
}