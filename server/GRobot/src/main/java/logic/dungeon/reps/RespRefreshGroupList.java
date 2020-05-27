package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.DungeonLevelGroupInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.RefreshDungeonLevelGroupList.MsgID.eMsgID_VALUE)
public class RespRefreshGroupList extends AbstractEvent {

    public RespRefreshGroupList(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;

        S2CDungeonMsg.RefreshDungeonLevelGroupList infoMsg =
                S2CDungeonMsg.RefreshDungeonLevelGroupList.parseFrom(data);
        if (infoMsg.getGroupList().size() > 0) {
            for (DungeonLevelGroupInfo group : infoMsg.getGroupList()) {
                robot.getPlayer().getDungeonGroups().put(group.getCid(), group);
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "刷新关卡组信息--size:" + infoMsg.getGroupList().size());
        }
    }

}
