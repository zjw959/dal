package logic.maindating.resp;

import java.util.ArrayList;
import java.util.List;

import org.game.protobuf.s2c.S2CExtraDatingMsg;
import org.game.protobuf.s2c.S2CExtraDatingMsg.EntranceInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.MAINDATING,
        order = S2CExtraDatingMsg.RespMainInfo.MsgID.eMsgID_VALUE)
public class ResMainDatingInfoEvent extends AbstractEvent {

    public ResMainDatingInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CExtraDatingMsg.RespMainInfo msg = S2CExtraDatingMsg.RespMainInfo.parseFrom(data);
            List<EntranceInfo> entraces = msg.getInfo().getEntrancesList();
            List<Integer> entraceIds = new ArrayList<Integer>();
            entraces.forEach(e -> entraceIds.add(e.getEntranceId()));
            player.setEntrances(entraceIds);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到主线约会入口信息  " + entraces);
        }
    }

}
