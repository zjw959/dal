package logic.maindating.req;

import java.util.List;
import java.util.Random;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.maindating.ReqMaindatingOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAINDATING,
        order = ReqMaindatingOrder.REQ_CHOOSE_INFO)
public class ReqChooseEvent extends AbstractEvent{

    public ReqChooseEvent(RobotThread robot) {
        super(robot, RespEntranceEventChoosed.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ReqChooseEntranceEvent.Builder builder = ReqChooseEntranceEvent.newBuilder();
        builder.setDatingType(2);
        builder.setDatingValue(1010101);
        builder.setEntranceId(robot.getPlayer().getChooseEntranceId());
        builder.setChoiceType(1);
        List<Integer> choices = robot.getPlayer().getChoices();
        if (choices.size() == 0) {
            return;
        }
        Random random = new Random();
        Integer choiceId = random.nextInt(choices.size()) + 1;
        builder.setChoice(choiceId);
        builder.setEventId(robot.getPlayer().choiceId);
        SMessage msg = new SMessage(ReqChooseEntranceEvent.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "选择主线约会选项信息 ReqChooseEntranceEvent");
    }

}
