package logic.role.req;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.role.ReqRoleOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ROLE,
        order = ReqRoleOrder.REQ_DONATE_ITEM)
public class ReqDonateItemEvent extends AbstractEvent {

    public ReqDonateItemEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = this.robot.getPlayer();

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        str.append("534011").append(":").append("1");

        if (player.getRoleMap().size() < 2) {
            str.append(",");
            str.append("110301").append(":").append("1");
        }

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
