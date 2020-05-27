package logic.chasmfight.req;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasmfight.ReqChasmFightOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM_FIGHT,
        order = ReqChasmFightOrder.REQ_OPERATE_FIGHT)
public class ReqOperateFightEvent extends AbstractEvent {

    public ReqOperateFightEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        if (teamInfo != null) {
            if(robot.isKcp() && robot.getKcpClient() != null && robot.getKcpClient().isRunning()) {
                C2SFightMsg.ReqOperateFight.Builder reqOperateFightBuilder =
                        C2SFightMsg.ReqOperateFight.newBuilder();
                reqOperateFightBuilder.setKeyCode(20);
                reqOperateFightBuilder.setKeyEvent(5);
                SMessage msg = new SMessage(C2SFightMsg.ReqOperateFight.MsgID.eMsgID_VALUE,
                        reqOperateFightBuilder.build().toByteArray(), resOrder);           
                sendMsg(msg, true);
            } else if(!robot.isKcp() && robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                C2SFightMsg.ReqOperateFight.Builder reqOperateFightBuilder =
                        C2SFightMsg.ReqOperateFight.newBuilder();
                reqOperateFightBuilder.setKeyCode(20);
                reqOperateFightBuilder.setKeyEvent(5);
                SMessage msg = new SMessage(C2SFightMsg.ReqOperateFight.MsgID.eMsgID_VALUE,
                        reqOperateFightBuilder.build().toByteArray(), resOrder);           
                sendMsg(msg, true);
            } else {
                super.robotSkipRun();
            }
        } else {
            super.robotSkipRun();
        }
    }

}
