package logic.comment.req;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2SCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.comment.ReqCommentOrder;
import utils.RandomEx;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.COMMENT,
        order = ReqCommentOrder.REQ_COMMENT_LIST)
public class ReqCommentEvent extends AbstractEvent {

    public ReqCommentEvent(RobotThread robot) {
        super(robot, S2CCommentMsg.RespComment.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        Map<Integer, HeroInfo> heroInfos = robot.getPlayer().getHeros();
        Map<String, EquipmentInfo> _equips = robot.getPlayer().getEquipmentKV();
        if ((heroInfos == null || heroInfos.isEmpty()) && (_equips == null || _equips.isEmpty())) {
            robotSkipRun();
            return;
        }
        int _type = RandomEx.nextInt(10000) % 2 + 1;
        if (heroInfos == null || heroInfos.isEmpty()) {
            _type = 1;
        } else if (_equips == null || _equips.isEmpty()) {
            _type = 2;
        }
        int id = 0;
        if (_type == 1) {
            id = getRandomEquipId(_equips.values());
        } else {
            id = getRandomHeroId(heroInfos.values());
        }

        C2SCommentMsg.ReqComment.Builder reqGetFriendList = C2SCommentMsg.ReqComment.newBuilder();
        reqGetFriendList.setItemId(id);
        reqGetFriendList.setType(_type);
        SMessage msg = new SMessage(C2SCommentMsg.ReqComment.MsgID.eMsgID_VALUE,
                reqGetFriendList.build().toByteArray(), resOrder);
        sendMsg(msg);
        return;
    }

    private int getRandomHeroId(Collection<HeroInfo> heros) {
        List<HeroInfo> _list = new ArrayList<HeroInfo>(heros);
        Collections.shuffle(_list);
        return _list.get(0).getCid();
    }

    private int getRandomEquipId(Collection<EquipmentInfo> equips) {
        List<EquipmentInfo> _list = new ArrayList<EquipmentInfo>(equips);
        Collections.shuffle(_list);
        return _list.get(0).getCid();
    }
}
