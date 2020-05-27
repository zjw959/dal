package logic.hero.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.HeroExpItem;
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
        order = ReqHeroOrder.REQ_UPGRADE_HERO)
public class ReqUpgradeHeroEvent extends AbstractEvent {

    public ReqUpgradeHeroEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.HeroUpgradeResult.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        int upgradeHeroId = robotPlayer.upgradeHeroId;
        if(upgradeHeroId == 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求升级英雄，已达到最大级！");
            super.robotSkipRun();
            return;
        } else {
            C2SHeroMsg.HeroUpgrade.Builder upgradeHeroMsg = C2SHeroMsg.HeroUpgrade.newBuilder();
            upgradeHeroMsg.setHeroId(String.valueOf(upgradeHeroId));
            HeroExpItem.Builder expItemBuilder = HeroExpItem.newBuilder();
            expItemBuilder.setItemId(robotPlayer.heroExpItem);
            expItemBuilder.setNum(1);
            upgradeHeroMsg.addItems(expItemBuilder);
            SMessage msg = new SMessage(C2SHeroMsg.HeroUpgrade.MsgID.eMsgID_VALUE,
                    upgradeHeroMsg.build().toByteArray(), resOrder);
            sendMsg(msg);    
        }
    }
}
