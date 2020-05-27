package logic.hero.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.ReqChangeHeroSkin;
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
        order = ReqHeroOrder.REQ_CHANGE_HERO_SKIN)
public class ReqChangeHeroSkinEvent extends AbstractEvent {

    public ReqChangeHeroSkinEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.RespChangeHeroSkin.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        String skinId = robotPlayer.getEntityId(robotPlayer.changeSkinId);
        if(robotPlayer.changeSkinHeroId != 0 && skinId != null) {
            ReqChangeHeroSkin.Builder reqChangeHeroSkinBuilder = ReqChangeHeroSkin.newBuilder();
            reqChangeHeroSkinBuilder.setHeroId(String.valueOf(robotPlayer.changeSkinHeroId));
            reqChangeHeroSkinBuilder.setSkinId(skinId);
            SMessage msg = new SMessage(C2SHeroMsg.ReqChangeHeroSkin.MsgID.eMsgID_VALUE,
                    reqChangeHeroSkinBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "无法更换皮肤");
            super.robotSkipRun();
			return;
        }
    }
}
