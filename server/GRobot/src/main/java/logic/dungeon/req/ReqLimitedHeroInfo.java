package logic.dungeon.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_LIMITED_HEROS)
public class ReqLimitedHeroInfo extends AbstractEvent {

    public ReqLimitedHeroInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.LimitHeroDungeonMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // 获取可用的
        DungeonLevelCfgBean cfg;
        List<Integer> ids = new ArrayList<Integer>(robot.getPlayer().getDungeons().keySet());
        Collections.shuffle(ids);
        int chosenId = -1;
        for (Integer lid : ids) {
            cfg = GameDataManager.getDungeonLevelCfgBean(lid);
            if (cfg.getHeroLimitID() == null || cfg.getHeroLimitID().length <= 0)
                continue;
            chosenId = lid;
            break;
        }
        if (chosenId < 0) {
            super.robotSkipRun();
            return;
        }
        C2SDungeonMsg.LimitHeroDungeonMsg.Builder build =
                C2SDungeonMsg.LimitHeroDungeonMsg.newBuilder();
        build.setLevelId(chosenId);
        SMessage msg =
                new SMessage(C2SDungeonMsg.LimitHeroDungeonMsg.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg);
    }
}
