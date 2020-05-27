package logic.comment.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2SCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg.CommentInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.comment.ReqCommentOrder;
import utils.RandomEx;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.COMMENT,
        order = ReqCommentOrder.REQ_FRIEND_LIKE)
public class ReqCommentLikeEvent extends AbstractEvent {

    public ReqCommentLikeEvent(RobotThread robot) {
        super(robot, S2CCommentMsg.RespPrise.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        Map<Integer, List<CommentInfo>> _hot = robot.getPlayer().getHotComment();
        Map<Integer, List<CommentInfo>> _new = robot.getPlayer().getNewComment();
        if ((_hot == null || _hot.isEmpty()) && (_new == null || _new.isEmpty())) {
            robotSkipRun();
            return;
        }

        int _type = RandomEx.nextInt(10000) % 2 + 1;
        if (_hot == null || _hot.isEmpty()) {
            _type = 1;
        } else if (_new == null || _new.isEmpty()) {
            _type = 2;
        }
        CommentInfo id = null;
        if (_type == 2) {
            id = getCommentInfo(_hot);
        } else {
            id = getCommentInfo(_new);
        }
        C2SCommentMsg.ReqPrise.Builder reqGetFriendList = C2SCommentMsg.ReqPrise.newBuilder();
        reqGetFriendList.setItemId(id.getItemId());
        reqGetFriendList.setPlayerId(id.getPlayerId());
        reqGetFriendList.setCommentDate(id.getCommentDate());
        reqGetFriendList.setType(RandomEx.nextInt(10000) % 2 + 1);
        SMessage msg = new SMessage(C2SCommentMsg.ReqPrise.MsgID.eMsgID_VALUE,
                reqGetFriendList.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

    private CommentInfo getCommentInfo(Map<Integer, List<CommentInfo>> infos) {
        List<List<CommentInfo>> _s = new ArrayList<>(infos.values());
        Collections.shuffle(_s);
        return _s.get(0).get(0);
    }
}
