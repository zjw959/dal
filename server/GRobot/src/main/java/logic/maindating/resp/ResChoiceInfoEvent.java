package logic.maindating.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CExtraDatingMsg.RespChoices;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.MAINDATING,
        order = RespChoices.MsgID.eMsgID_VALUE)
public class ResChoiceInfoEvent extends AbstractEvent {

    public ResChoiceInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            RespChoices msg = RespChoices.parseFrom(data);
            List<Integer> choicesIdList = msg.getEventIdList();
            player.setChoices(choicesIdList);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到选项信息 " + choicesIdList);
        }
    }

}
