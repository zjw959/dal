package logic.chasmfight.req;

import org.game.protobuf.c2s.C2SChasmMsg;
import org.game.protobuf.c2s.C2SChasmMsg.ReqChasmStartFight;
import org.game.protobuf.s2c.S2CChasmMsg;
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
        order = ReqChasmFightOrder.REQ_CHASM_START_FIGHT)
public class ReqChasmStartFightEvent extends AbstractEvent {

    public ReqChasmStartFightEvent(RobotThread robot) {
        super(robot, S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        if (teamInfo != null && playerInfo.getPid() == teamInfo.getLeaderPid()) {
            ReqChasmStartFight.Builder reqChasmStartFightBuilder = ReqChasmStartFight.newBuilder();
            SMessage msg = new SMessage(C2SChasmMsg.ReqChasmStartFight.MsgID.eMsgID_VALUE,
                    reqChasmStartFightBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }

}
