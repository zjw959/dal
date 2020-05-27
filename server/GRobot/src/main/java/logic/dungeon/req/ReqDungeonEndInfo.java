package logic.dungeon.req;

import java.util.List;
import java.util.stream.Collectors;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_DUNGEON_END)
public class ReqDungeonEndInfo extends AbstractEvent {

    public ReqDungeonEndInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.FightOverMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        DungeonLevelCfgBean levelCfg =
                GameDataManager.getDungeonLevelCfgBean(robot.getPlayer().getDungeonLevelId());
        if (levelCfg == null) {
            // 请求order列表
            List<Integer> reqList = null;
            if (robot.getReqQueue() != null) {
                reqList = robot.getReqQueue().stream().map(event -> {
                    IsEvent isEventClazz = event.getClass().getAnnotation(IsEvent.class);
                    return isEventClazz.order();
                }).collect(Collectors.toList());
            }
            Log4jManager.getInstance().warn(
                    robot.getWindow(),
                    "exception null dungeonlevel list, reqOrders " + reqList + ", repsId:"
                            + robot.getResOrder() + ", levelCid:"
                            + robot.getPlayer().getDungeonLevelId());
            super.robotSkipRun();
            return;
        }
        C2SDungeonMsg.FightOverMsg.Builder build = C2SDungeonMsg.FightOverMsg.newBuilder();
        build.setLevelCid(levelCfg.getId());
        build.setBatter((int) (Math.random() * 200));
        build.setIsWin(true);
        if (levelCfg.getStarType() != null) {
            for (int goal : levelCfg.getStarType())
                build.addGoals(goal);
        }
        int pickCount = (int) (Math.random() * 20);
        build.setPickUpCount(pickCount);
        build.setPickUpTypeCount((int) (Math.random() * pickCount));
        SMessage msg =
                new SMessage(C2SDungeonMsg.FightOverMsg.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg);
    }
}
