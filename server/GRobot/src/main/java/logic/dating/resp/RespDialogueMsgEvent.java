package logic.dating.resp;

import org.game.protobuf.s2c.S2CDatingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;


/****
 * 返回对话
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DATING,
        order = S2CDatingMsg.DialogueMsg.MsgID.eMsgID_VALUE)
public class RespDialogueMsgEvent extends AbstractEvent {

    public RespDialogueMsgEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
//        byte[] data = (byte[]) obj[0];
//        if (data.length > 0) {
//            RobotPlayer player = robot.getPlayer();
//            remove(player);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "对话成功");
//        } else {
//            Log4jManager.getInstance().debug(robot.getWindow(),
//                    "robot:" + robot.getName() + "对话失败");
//        }
    }
    
//    /**移除**/
//    private void remove(RobotPlayer player) {
//        List<BranchNode> removeList = new ArrayList<BranchNode>();
//        for (int i = 0; i < player.getBranchNodeList().size(); i++) {
//            if (i != player.getBranchNodeList().size()-1) {
//                removeList.add(player.getBranchNodeList().get(i));
//            }
//        }
//        player.setBranchNodeList(removeList);
//    }
//    

}
