package logic.chasmfight.req;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqEnterFight;
import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CTeamMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasmfight.ReqChasmFightOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM_FIGHT,
        order = ReqChasmFightOrder.REQ_ENTER_FIGHT)
public class ReqEnterFightEvent extends AbstractEvent {

    public ReqEnterFightEvent(RobotThread robot) {
        super(robot, S2CFightMsg.NotifyStartFight.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        if(teamInfo != null) {
            if(robot.isKcp() && robot.getKcpClient() != null && robot.getKcpClient().isRunning()) {
                S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
                ReqEnterFight.Builder reqEnterFight = ReqEnterFight.newBuilder();
                reqEnterFight.setFightId(robotPlayer.fightId);
                reqEnterFight.setPid(playerInfo.getPid());
                SMessage msg = new SMessage(C2SFightMsg.ReqEnterFight.MsgID.eMsgID_VALUE,
                        reqEnterFight.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else if(!robot.isKcp() && robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
                ReqEnterFight.Builder reqEnterFight = ReqEnterFight.newBuilder();
                reqEnterFight.setFightId(robotPlayer.fightId);
                reqEnterFight.setPid(playerInfo.getPid());
                SMessage msg = new SMessage(C2SFightMsg.ReqEnterFight.MsgID.eMsgID_VALUE,
                        reqEnterFight.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else {
                super.robotSkipRun(); 
            }
        } else {
            super.robotSkipRun();
        }
    }

}