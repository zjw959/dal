package logic.dungeon.req;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

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
        order = DungeonOrder.REQ_RWARD_COST_BACK)
public class ReqDungeonCostBackInfo extends AbstractEvent {

    public ReqDungeonCostBackInfo(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        addLevelCostBack(robot.getPlayer().getDungeonLevelId());
    }

    @SuppressWarnings("unchecked")
    private void addLevelCostBack(int levelCid) {
        DungeonLevelCfgBean levelCfg = GameDataManager.getDungeonLevelCfgBean(levelCid);
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
        if(levelCfg.getCost() == null) {
            super.robotSkipRun();
            return;
        }
        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        for (Entry<Integer, Integer> goodType : ((Map<Integer, Integer>) levelCfg.getCost())
                .entrySet()) {
            str.append(goodType.getKey());
            str.append(":");
            str.append(goodType.getValue());
            str.append(",");
        }
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg =
                new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE, builder.build().toByteArray(),
                        this.resOrder);
        sendMsg(msg);
    }
}
