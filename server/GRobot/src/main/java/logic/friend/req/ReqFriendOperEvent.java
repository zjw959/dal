package logic.friend.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.game.protobuf.c2s.C2SFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.friend.FriendConstants;
import logic.friend.ReqFriendOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.FRIEND,
        order = ReqFriendOrder.REQ_FRIEND_OPER)
public class ReqFriendOperEvent extends AbstractEvent {

    public ReqFriendOperEvent(RobotThread robot) {
        super(robot, S2CFriendMsg.RespOperate.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // Log4jManager.getInstance().error(robot.getWindow(), "ReqFriendOperEvent");
        Map<Integer, FriendInfo> _friends = robot.getPlayer().getFriendInfos();
        List<FriendInfo> friendList = new ArrayList<FriendInfo>(_friends.values());
        // 好友列表为空，拿好友推荐列表一个玩家数据
        if (friendList == null || friendList.isEmpty()) {
            List<FriendInfo> _recommands =
                    new ArrayList<FriendInfo>(robot.getPlayer().getRecommendFriendInfos().values());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到好友推荐消息 " + _recommands);
            if (_recommands == null || _recommands.isEmpty()) {
                robotSkipRun();
                return;
            }
            int fristFriendId = _recommands.get(0).getPid();
            // 随机取一个推荐 做申请好友操作
            C2SFriendMsg.ReqOperate.Builder reqGetFriendList = C2SFriendMsg.ReqOperate.newBuilder();
            reqGetFriendList.addTargets(fristFriendId);
            reqGetFriendList.setType(FriendConstants.OPERATE_APPLY_FRIEND);
            SMessage msg = new SMessage(C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE,
                    reqGetFriendList.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到好友推荐消息，并顺便申请了加了一个好友 " + fristFriendId);
            return;
        }


        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "好友列表中已有数据");
        // 收到好友列表消息后 如果有申请好友的玩家
        // 服务器收到好友申请消息70%概率同意 ，否则拒绝
        for (FriendInfo friend : friendList) {
            if (friend.getCt().equals(ChangeType.DELETE)) {
                continue;
            }
            // 状态:1:好友,2:屏蔽,3:申请
            if (friend.getStatus() == 3 && (friend.getCt().equals(ChangeType.ADD)
                    || friend.getCt().equals(ChangeType.DEFAULT))) {
                C2SFriendMsg.ReqOperate.Builder operMsg = C2SFriendMsg.ReqOperate.newBuilder();
                operMsg.addTargets(friend.getPid());
                if (new Random().nextInt(10) < 7) {
                    operMsg.setType(FriendConstants.OPERATE_AGREE_APPLY);
                    Log4jManager.getInstance().debug(robot.getWindow(),
                            "robot:" + robot.getName() + "同意好友申请 " + friend.getPid());
                } else {
                    operMsg.setType(FriendConstants.OPERATE_REFUSE_APPLY);
                    Log4jManager.getInstance().debug(robot.getWindow(),
                            "robot:" + robot.getName() + "拒绝好友申请 " + friend.getPid());
                }
                sendMsg(new SMessage(C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE,
                        operMsg.build().toByteArray(), resOrder));
                return;
            }
        }
        // 收到好友列表消息 可以做的操作 查看好友、删除好友、赠送礼品
        int randNum = new Random().nextInt(70);
        int randomPid = friendList.get(new Random().nextInt(friendList.size())).getPid();
        // 20%概率查看好友
        /*
         * if (randNum < 20) { C2SFriendMsg.ReqQueryPlayer.Builder operMsg =
         * C2SFriendMsg.ReqQueryPlayer.newBuilder(); operMsg.setPid(randomPid); SMessage msg = new
         * SMessage(C2SFriendMsg.ReqQueryPlayer.MsgID.eMsgID_VALUE, operMsg.build().toByteArray(),
         * resOrder); sendMsg(msg, true); Log4jManager.getInstance().debug(robot.getWindow(),
         * "robot:" + robot.getName() + " 查看好友信息 " + randomPid); // 10%概率删除好友 } else
         */ if (randNum < 30) {
            C2SFriendMsg.ReqOperate.Builder operMsg = C2SFriendMsg.ReqOperate.newBuilder();
            operMsg.addTargets(randomPid);
            operMsg.setType(FriendConstants.OPERATE_DELETE_FRIEND);
            SMessage msg = new SMessage(C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE,
                    operMsg.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + " 发起删除好友请求 " + randomPid);
            // 20%概率赠送礼品
        } else if (randNum < 50) {
            C2SFriendMsg.ReqOperate.Builder operMsg = C2SFriendMsg.ReqOperate.newBuilder();
            operMsg.addTargets(randomPid);
            operMsg.setType(FriendConstants.OPERATE_GIVE_GIFT);
            SMessage msg = new SMessage(C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE,
                    operMsg.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + " 发起送礼请求 " + randomPid);
            // 20%概率收礼
        } else {
            FriendInfo _friend = _friends.get(randomPid);
            if (_friend == null) {
                robotSkipRun();
                return;
            }
            if (!_friend.getReceive()) {
                /*
                 * C2SFriendMsg.ReqQueryPlayer.Builder operMsg =
                 * C2SFriendMsg.ReqQueryPlayer.newBuilder(); operMsg.setPid(randomPid); SMessage msg
                 * = new SMessage(C2SFriendMsg.ReqQueryPlayer.MsgID.eMsgID_VALUE,
                 * operMsg.build().toByteArray(), resOrder); sendMsg(msg, true);
                 * Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() +
                 * " 不可领取礼物则查看好友信息 " + randomPid);
                 */
                robotSkipRun();
                return;
            }
            C2SFriendMsg.ReqOperate.Builder operMsg = C2SFriendMsg.ReqOperate.newBuilder();
            operMsg.addTargets(randomPid);
            operMsg.setType(FriendConstants.OPERATE_RECEIVE_GIFT);
            SMessage msg = new SMessage(C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE,
                    operMsg.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + " 发起收礼请求 " + randomPid);
            // 30% 概率私聊
        } /*
           * else { FriendInfo _friend = _friends.get(randomPid); if (_friend == null) {
           * robotSkipRun(); return; } C2SChatMsg.ChatMsg.Builder _chatMsg =
           * C2SChatMsg.ChatMsg.newBuilder(); _chatMsg.setChannel(2);// 私聊 _chatMsg.setContent(
           * "robot chat from " + robot.getName()); _chatMsg.setPlayerId(randomPid);
           * _chatMsg.setFun(1); SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
           * _chatMsg.build().toByteArray(), resOrder); sendMsg(msg, true);
           * Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() +
           * " 发送聊天信息 " + randomPid); }
           */
    }
}
