package logic.equip.req;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.equip.ReqEquipOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
        order = ReqEquipOrder.REQ_GM_EQUIP)
public class ReqEquipItemEvent extends AbstractEvent {

    public ReqEquipItemEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        str.append("240047").append(":").append("4").append(",");
        str.append("500001").append(":").append("150");
        String context = str.toString();
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(context);
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }

}
