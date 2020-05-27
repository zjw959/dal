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
import data.bean.DungeonLevelGroupCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_BUY_COUNTS)
public class ReqCountPurchaseInfo extends AbstractEvent {

    public ReqCountPurchaseInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.BuyFightCount.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // 获取可用的
        List<S2CDungeonMsg.DungeonLevelGroupInfo> infos =
                new ArrayList<>(robot.getPlayer().getDungeonGroups().values());
        Collections.shuffle(infos);
        int chosenId = -1;
        for (S2CDungeonMsg.DungeonLevelGroupInfo groupInfo : infos) {
            DungeonLevelGroupCfgBean groupCfg =
                    GameDataManager.getDungeonLevelGroupCfgBean(groupInfo.getCid());
            if (groupCfg.getBuyCountLimit() <= groupInfo.getBuyCount())
                continue;
            chosenId = groupCfg.getId();
        }
        if (chosenId < 0) {
            super.robotSkipRun();
            return;
        }
        C2SDungeonMsg.BuyFightCount.Builder build = C2SDungeonMsg.BuyFightCount.newBuilder();
        build.setCid(chosenId);
        SMessage msg =
                new SMessage(C2SDungeonMsg.BuyFightCount.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg);
    }
}
