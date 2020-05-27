package logic.store.resp;

import org.game.protobuf.s2c.S2CStoreMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.STORE,
        order = S2CStoreMsg.StoreDataInfo.MsgID.eMsgID_VALUE)
public class RespStoreListEvent extends AbstractEvent {

    public RespStoreListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] date = (byte[]) obj[0];
        if (date.length > 0) {
            S2CStoreMsg.StoreDataInfo dataInfo = S2CStoreMsg.StoreDataInfo.parseFrom(date);
            RobotPlayer player = robot.getPlayer();
            player.updateStroe(dataInfo.getStoresList());
        }
    }

}
