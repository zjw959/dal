package logic.comment.handler;

import org.game.protobuf.c2s.C2SCommentMsg;

import exception.AbstractLogicModelException;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.comment.CommentManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SCommentMsg.ReqComment.class)
public class MReqCommentHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }

        C2SCommentMsg.ReqComment msg = (C2SCommentMsg.ReqComment) getMessage().getData();
        CommentManager manager = player.getCommentManager();
        manager.getComment(msg.getItemId(), msg.getType());
    }
}
