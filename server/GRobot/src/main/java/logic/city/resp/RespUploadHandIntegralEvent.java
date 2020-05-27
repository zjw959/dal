package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

/***
 * 
 * 上传手工积分回调
 * @author
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespUploadHandIntegral.MsgID.eMsgID_VALUE)
public class RespUploadHandIntegralEvent extends AbstractEvent {

    public RespUploadHandIntegralEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {

        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            
            
            }
        }
}
