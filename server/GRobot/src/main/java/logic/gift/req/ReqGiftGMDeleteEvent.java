package logic.gift.req;

import org.apache.commons.lang.StringUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.gift.ReqGiftOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.GIFT,
        order = ReqGiftOrder.REQ_GM_DELETE)
public class ReqGiftGMDeleteEvent extends AbstractEvent {

    public ReqGiftGMDeleteEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append("./delItemList ");
        str.append("500001").append(":").append("1000").append(",");
        str.append("500002").append(":").append("20000").append(",");

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
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "删除礼包奖励");
        }

    }

}
