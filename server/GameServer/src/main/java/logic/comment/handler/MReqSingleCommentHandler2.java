package logic.comment.handler;

import org.game.protobuf.c2s.C2SCommentMsg;

import exception.AbstractLogicModelException;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.comment.CommentManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SCommentMsg.ReqSingleComment.class)
public class MReqSingleCommentHandler2 extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }

        C2SCommentMsg.ReqSingleComment msg =
                (C2SCommentMsg.ReqSingleComment) getMessage().getData();
        CommentManager manager = player.getCommentManager();
        manager.addComment(msg.getItemId(), msg.getComment(), msg.getType());
    }
}
