package logic.dungeon.reps;

import java.util.List;

import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.DungeonLevelGroupInfo;
import org.game.protobuf.s2c.S2CShareMsg.ListMap;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.GetLevelGroupReward.MsgID.eMsgID_VALUE)
public class RespGroupRewardInfo extends AbstractEvent {

    public RespGroupRewardInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CDungeonMsg.GetLevelGroupReward infoMsg =
                S2CDungeonMsg.GetLevelGroupReward.parseFrom(data);
        int groupCid = infoMsg.getCid();
        List<ListMap> rewardInfo = infoMsg.getRewardInfoList();
        robot.getPlayer()
                .getDungeonGroups()
                .put(groupCid,
                        DungeonLevelGroupInfo.newBuilder().setCid(groupCid)
                                .addAllRewardInfo(rewardInfo).setFightCount(0).setId("")
                                .setMainLineCid(0).setMaxMainLine(0).build());
        // 不需要的临时数据
        Log4jManager.getInstance().debug(robot.getWindow(),
                "领取关卡组奖励--group:" + groupCid + ", info:" + rewardInfo);
    }

}
