package logic.friend.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;
import org.game.protobuf.s2c.S2CPlayerMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.friend.ReqFriendOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.FRIEND,
        order = ReqFriendOrder.REQ_QUERY_PLAYER)
public class ReqQueryPlayerEvent extends AbstractEvent {

    public ReqQueryPlayerEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // Log4jManager.getInstance().error(robot.getWindow(),"ReqQueryPlayerEvent");
        Map<Integer, FriendInfo> _friends = robot.getPlayer().getFriendInfos();
        if (_friends != null && !_friends.isEmpty()) {
            searchPlayer(_friends);
            return;
        }
        Map<Integer, FriendInfo> _recommendFriends = robot.getPlayer().getRecommendFriendInfos();
        if (_recommendFriends.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "好友推荐信息为空");
            robotSkipRun();
            return;
        } else {
            searchPlayer(_recommendFriends);
            return;
        }
    }

    private void searchPlayer(Map<Integer, FriendInfo> friends) {
        
        List<FriendInfo> friendList = new ArrayList<FriendInfo>(friends.values());
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "收到好友推荐消息 " + friendList);
        int fristFriendId = friendList.get(0).getPid();
        // 随机取一个推荐 做申请好友操作
        C2SPlayerMsg.ReqTargetPlayerInfo.Builder reqGetFriendList =
                C2SPlayerMsg.ReqTargetPlayerInfo.newBuilder();
        reqGetFriendList.setTargetPid(fristFriendId);
        SMessage msg = new SMessage(S2CPlayerMsg.RespTargetPlayerInfo.MsgID.eMsgID_VALUE,
                reqGetFriendList.build().toByteArray(),resOrder);
        sendMsg(msg, true);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "收到好友推荐消息，并顺便申请了加了一个好友 " + fristFriendId);
    }
}
