package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.BuyFightCount.MsgID.eMsgID_VALUE)
public class RespCountPurchaseInfo extends AbstractEvent {

    public RespCountPurchaseInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CDungeonMsg.BuyFightCount infoMsg = S2CDungeonMsg.BuyFightCount.parseFrom(data);
        // 不需要的临时数据
        Log4jManager.getInstance().debug(robot.getWindow(), "购买关卡组次数成功--group:" + infoMsg.getCid());
    }

}
