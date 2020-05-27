package logic.chasm.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CChasmMsg;
import org.game.protobuf.s2c.S2CChasmMsg.ChasmInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CChasmMsg.RsepEnterChasm.MsgID.eMsgID_VALUE)
public class RespEnterChasmEvent extends AbstractEvent {

    public RespEnterChasmEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CChasmMsg.RsepEnterChasm respEnterChasm = S2CChasmMsg.RsepEnterChasm.parseFrom(data);
            List<ChasmInfo> chasmInfo = respEnterChasm.getChashsList();
            robotPlayer.updateChasmInfo(chasmInfo);
        }
    }

}
