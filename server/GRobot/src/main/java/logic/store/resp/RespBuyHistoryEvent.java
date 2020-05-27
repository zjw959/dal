package logic.store.resp;

import org.game.protobuf.s2c.S2CStoreMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CStoreMsg.CommodityBuyLogs.MsgID.eMsgID_VALUE)
public class RespBuyHistoryEvent extends AbstractEvent {

    public RespBuyHistoryEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CStoreMsg.CommodityBuyLogs msg = S2CStoreMsg.CommodityBuyLogs.parseFrom(data);
            player.updateBuyHistory(msg.getBuyLogsList());
        }
    }

}
