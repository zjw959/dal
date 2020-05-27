package logic.gift.resp;

import org.game.protobuf.s2c.S2CLoginMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CLoginMsg.GiftCodeRps.MsgID.eMsgID_VALUE)
public class RespGiftEvent extends AbstractEvent {

    public RespGiftEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        long time = System.currentTimeMillis() - player.getGiftSendTime();
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "回调礼包耗时：" + time + "ms");
    }


}
