package logic.maindating.resp;

import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.MAINDATING,
        order = RespEntranceEventChoosed.MsgID.eMsgID_VALUE)
public class ResChooseEvent extends AbstractEvent {

    public ResChooseEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RespEntranceEventChoosed msg = RespEntranceEventChoosed.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到节点返回信息 " + msg);
        }
    }

}
