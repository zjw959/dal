package logic.chasmfight.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CFightMsg.DataFrame;
import org.game.protobuf.s2c.S2CFightMsg.NetFrame;
import org.game.protobuf.s2c.S2CFightMsg.OperateFrame;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFightMsg.NotifyNetFrame.MsgID.eMsgID_VALUE)
public class NotifyNetFrameEvent extends AbstractEvent {

    public NotifyNetFrameEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CFightMsg.NotifyNetFrame notifyNetFrame = S2CFightMsg.NotifyNetFrame.parseFrom(data);
            NetFrame netFrame = notifyNetFrame.getNetFrame();
            List<OperateFrame> operateFrame = netFrame.getOperateFrameList();
            List<DataFrame> dataFrame = netFrame.getDataFrameList();
            int dataSize = operateFrame.size() + dataFrame.size();
            robot.getWindow().chasmReceiveMsgs.addAndGet(dataSize);
        }
    }

}
