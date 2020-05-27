package logic.comment.resp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg.CommentInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CCommentMsg.RespComment.MsgID.eMsgID_VALUE)
public class ResCommentOperEvent extends AbstractEvent {

    public ResCommentOperEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CCommentMsg.RespComment friendListMsg = S2CCommentMsg.RespComment.parseFrom(data);
            List<CommentInfo> _hotList = friendListMsg.getHotInfoList();
            List<CommentInfo> _newList = friendListMsg.getNewInfoList();
            if (_hotList == null && _newList == null) {
                return;
            }
            Map<Integer, List<CommentInfo>> _hotMap = new HashMap<>();
            Map<Integer, List<CommentInfo>> _newMap = new HashMap<>();

            if (_hotList != null) {
                for (CommentInfo _info : _hotList) {
                    List<CommentInfo> _temps = _hotMap.get(_info.getItemId());
                    if (_temps == null) {
                        _temps = new ArrayList<CommentInfo>();
                        _hotMap.put(_info.getItemId(), _temps);
                    }
                    _temps.add(_info);
                }
            }
            if (_newList != null) {
                for (CommentInfo _info : _newList) {
                    List<CommentInfo> _temps = _newMap.get(_info.getItemId());
                    if (_temps == null) {
                        _temps = new ArrayList<CommentInfo>();
                        _newMap.put(_info.getItemId(), _temps);
                    }
                    _temps.add(_info);
                }
            }
            robot.getPlayer().setHotComment(_hotMap);
            robot.getPlayer().setNewComment(_newMap);
        }
    }
}
