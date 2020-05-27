package logic.dating.handler.message;

import java.util.Date;

import logic.character.bean.Player;
import logic.dating.DatingService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;

import exception.AbstractLogicModelException;

/**
 * 获取约会信息
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDatingMsg.GetDatingInfo.class)
public class MGetDatingInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MGetDatingInfoHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        // 进行非正常约会的检测
        DatingService.getInstance().handleFailedCityDating(player, new Date());
        MessageUtils.send(player, DatingMsgBuilder.getDatingInfoMsg(player));
    }
}
