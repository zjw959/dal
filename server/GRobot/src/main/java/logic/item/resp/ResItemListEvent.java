package logic.item.resp;

import org.game.protobuf.s2c.S2CItemMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CItemMsg.ItemList.MsgID.eMsgID_VALUE)
public class ResItemListEvent extends AbstractEvent {

    public ResItemListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            if (robot.isInCleanBag) {

                robot.isInCleanBag = false;
            }

            S2CItemMsg.ItemList msg = S2CItemMsg.ItemList.parseFrom(data);
            RobotPlayer player = robot.getPlayer();
            player.updateItems(msg);
            
            // if(player.isSummonAward && player.summonAward != null) {
            // player.summonAward.add(msg);
            // }
        }
    }

}
