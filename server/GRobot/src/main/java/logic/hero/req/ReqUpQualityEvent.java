package logic.hero.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.hero.ReqHeroOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO,
        order = ReqHeroOrder.REQ_UP_QUALITY_HERO)
public class ReqUpQualityEvent extends AbstractEvent {

    public ReqUpQualityEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.RespUpQuality.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        int upQualityHeroId = robotPlayer.upQualityHeroId;
        if (upQualityHeroId == 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "英雄" + upQualityHeroId + "已达到最大品质");
            super.robotSkipRun();
        } else {
            C2SHeroMsg.ReqUpQuality.Builder reqUpQualityBuilder = C2SHeroMsg.ReqUpQuality.newBuilder();
            reqUpQualityBuilder.setHeroId(String.valueOf(upQualityHeroId));
            SMessage msg = new SMessage(C2SHeroMsg.ReqUpQuality.MsgID.eMsgID_VALUE,
                    reqUpQualityBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        }
    }
}
