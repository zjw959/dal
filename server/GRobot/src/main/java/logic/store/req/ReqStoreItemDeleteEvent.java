package logic.store.req;

import org.apache.commons.lang.StringUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.store.ReqStoreOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.STORE,
        order = ReqStoreOrder.DELETE_GOODS)
public class ReqStoreItemDeleteEvent extends AbstractEvent {

    public ReqStoreItemDeleteEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        long gold = player.getItemCount(500001);
        long good1 = player.getItemCount(570002);
        long good2 = player.getItemCount(570001);
        long good3 = player.getItemCount(570006);
        long good4 = player.getItemCount(570005);
        long good5 = player.getItemCount(500018);
        StringBuilder str = new StringBuilder();
        str.append("./delItemList ");
        str.append("500001").append(":").append(String.valueOf(gold)).append(",");
        str.append("570002").append(":").append(String.valueOf(good1)).append(",");
        str.append("570001").append(":").append(String.valueOf(good2)).append(",");
        str.append("570006").append(":").append(String.valueOf(good3)).append(",");
        str.append("500018").append(":").append(String.valueOf(good5)).append(",");
        str.append("570005").append(":").append(String.valueOf(good4));

        String gmContent = str.toString();
        int length = StringUtils.split(gmContent, " ").length;
        if (length > 1) {
            C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
            builder.setChannel(1);
            builder.setFun(1);
            builder.setContent(str.toString());
            SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), this.resOrder);
            sendMsg(msg);
        }

    }

}
