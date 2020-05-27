package logic.chasmfight.req;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.MemberData;
import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CPlayerMsg;
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
        order = ReqChasmFightOrder.REQ_END_FIGHT)
public class ReqEndFightEvent extends AbstractEvent {

    public ReqEndFightEvent(RobotThread robot) {
        super(robot, S2CFightMsg.RespEndFight.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        if(teamInfo != null) {
            if(robot.isKcp() && robot.getKcpClient() != null && robot.getKcpClient().isRunning()) {
                S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
                C2SFightMsg.ReqEndFight.Builder reqEndFightBuilder = C2SFightMsg.ReqEndFight.newBuilder();
                reqEndFightBuilder.setRandomSeed(robotPlayer.randomSeed);
                reqEndFightBuilder.setIsWin(true);
                MemberData.Builder memberDataBuilder = MemberData.newBuilder();
                memberDataBuilder.setPid(playerInfo.getPid());
                memberDataBuilder.setHurt(1000);
                reqEndFightBuilder.addMemberData(memberDataBuilder);
                reqEndFightBuilder.setFightTime(20);
                SMessage msg = new SMessage(C2SFightMsg.ReqEndFight.MsgID.eMsgID_VALUE,
                        reqEndFightBuilder.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else if(!robot.isKcp() && robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
                C2SFightMsg.ReqEndFight.Builder reqEndFightBuilder = C2SFightMsg.ReqEndFight.newBuilder();
                reqEndFightBuilder.setRandomSeed(robotPlayer.randomSeed);
                reqEndFightBuilder.setIsWin(true);
                MemberData.Builder memberDataBuilder = MemberData.newBuilder();
                memberDataBuilder.setPid(playerInfo.getPid());
                memberDataBuilder.setHurt(1000);
                reqEndFightBuilder.addMemberData(memberDataBuilder);
                reqEndFightBuilder.setFightTime(20);
                SMessage msg = new SMessage(C2SFightMsg.ReqEndFight.MsgID.eMsgID_VALUE,
                        reqEndFightBuilder.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else {
                super.robotSkipRun(); 
            }
        } else {
            super.robotSkipRun();
        }
    }

}
