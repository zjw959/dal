package logic.summon.resp;

import org.game.protobuf.s2c.S2CShareMsg;
import org.game.protobuf.s2c.S2CSummonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.Summon.MsgID.eMsgID_VALUE)
public class ResSummonEvent extends AbstractEvent {

    public ResSummonEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CSummonMsg.Summon resSummon = S2CSummonMsg.Summon.parseFrom(data);
            StringBuilder info = new StringBuilder();
            for(S2CShareMsg.RewardsMsg item : resSummon.getItemList()) {
                info.append(item.getId());
                info.append(":");
                info.append(item.getNum());
                info.append(",");
            }    
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "抽卡得到了 " + info.toString());   
        }
    }

}
