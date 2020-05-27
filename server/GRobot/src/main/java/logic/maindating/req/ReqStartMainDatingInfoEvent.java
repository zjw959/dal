package logic.maindating.req;

import java.util.List;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqStartEntranceEvent;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.maindating.ReqMaindatingOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAINDATING,
        order = ReqMaindatingOrder.REQ_START_INFO)
public class ReqStartMainDatingInfoEvent extends AbstractEvent {

    public ReqStartMainDatingInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ReqStartEntranceEvent.Builder builder = ReqStartEntranceEvent.newBuilder();
        builder.setDatingType(2);
        builder.setDatingValue(1010101);
        List<Integer> entrances = robot.getPlayer().getEntrances();
        if (entrances.size() == 0) {
            return;
        }
        int entranceId = entrances.get((int) Math.random() * entrances.size());
        builder.setEntranceId(entranceId);
        robot.getPlayer().setChooseEntranceId(entranceId);
        SMessage msg = new SMessage(ReqStartEntranceEvent.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg, true);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求开始进行主线约会  entranceId:" + entranceId);
    }

}
