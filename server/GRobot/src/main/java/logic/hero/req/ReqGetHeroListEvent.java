package logic.hero.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
        order = ReqOnceOrder.REQ_GET_HERO_LIST)
public class ReqGetHeroListEvent extends AbstractEvent {

    public ReqGetHeroListEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.HeroInfoList.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SHeroMsg.GetHeros.Builder reqGetHeroList = C2SHeroMsg.GetHeros.newBuilder();
        SMessage msg = new SMessage(C2SHeroMsg.GetHeros.MsgID.eMsgID_VALUE,
                reqGetHeroList.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
