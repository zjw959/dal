package logic.chasmfight.req;


import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasmfight.ReqChasmFightOrder;
import logic.chasmfight.unlimited.SendMsgService;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM_FIGHT,
        order = ReqChasmFightOrder.REQ_OPERATE_FIGHT_UNLIMITED)
public class ReqOperateFightUnlimitedEvent extends AbstractEvent {

    public ReqOperateFightUnlimitedEvent(RobotThread robot) {
        super(robot, S2CFightMsg.RespEndFight.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        if (teamInfo != null) {
            if(robot.isKcp() && robot.getKcpClient() != null && robot.getKcpClient().isRunning()) {
                boolean isUnlimited = robot.getWindow().getConsolePanel().isChasmCheckBox();
                if (isUnlimited) {
                    SendMsgService.unlimitedMsg.put(String.valueOf(robot.getKcpClient().getConv()), robot);
                    C2SFightMsg.ReqOperateFight.Builder reqOperateFightBuilder =
                            C2SFightMsg.ReqOperateFight.newBuilder();
                    reqOperateFightBuilder.setKeyCode(20);
                    reqOperateFightBuilder.setKeyEvent(5);
                    SMessage msg = new SMessage(C2SFightMsg.ReqOperateFight.MsgID.eMsgID_VALUE,
                            reqOperateFightBuilder.build().toByteArray(), resOrder);           
                    sendMsg(msg);
                } else {
                    super.robotSkipRun();
                }
            } else if(!robot.isKcp() && robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                boolean isUnlimited = robot.getWindow().getConsolePanel().isChasmCheckBox();
                if (isUnlimited) {
                    SendMsgService.unlimitedMsg.put(robot.getFightChannel().id().asLongText(), robot);
                    C2SFightMsg.ReqOperateFight.Builder reqOperateFightBuilder =
                            C2SFightMsg.ReqOperateFight.newBuilder();
                    reqOperateFightBuilder.setKeyCode(20);
                    reqOperateFightBuilder.setKeyEvent(5);
                    SMessage msg = new SMessage(C2SFightMsg.ReqOperateFight.MsgID.eMsgID_VALUE,
                            reqOperateFightBuilder.build().toByteArray(), resOrder);           
                    sendMsg(msg);
                } else {
                    super.robotSkipRun();
                }
            } else {
                super.robotSkipRun();
            }
        } else {
            super.robotSkipRun();
        }
    }

}
