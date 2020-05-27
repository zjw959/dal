package logic.store.resp;

import org.game.protobuf.s2c.S2CStoreMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.STORE,
        order = S2CStoreMsg.BuyGoods.MsgID.eMsgID_VALUE)
public class RespBuyGoodsEvent extends AbstractEvent {

    public RespBuyGoodsEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CStoreMsg.BuyGoods msg = S2CStoreMsg.BuyGoods.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName()
                    + "购买响应事件：  " + "Cid:" + msg.getCid() + " number:" + msg.getNum());

        }
    }

}
