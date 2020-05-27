package logic.chasmfight.req;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqCreateTeam;
import org.game.protobuf.c2s.C2STeamMsg.TeamFeature;
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
        order = ReqChasmFightOrder.REQ_CREATE_TEAM)
public class ReqCreateTeamEvent extends AbstractEvent {

    public ReqCreateTeamEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespCreateTeam.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        if(robotPlayer.chasmDungeonId != 0) {
            Thread.sleep(100);
            ReqCreateTeam.Builder reqCreateTeamBuilder = ReqCreateTeam.newBuilder();
            TeamFeature.Builder teamFeatureBuilder = TeamFeature.newBuilder();
            teamFeatureBuilder.setTeamType(1);
            teamFeatureBuilder.setDungeonCid(robotPlayer.chasmDungeonId);
            reqCreateTeamBuilder.setFeature(teamFeatureBuilder);
            SMessage msg = new SMessage(C2STeamMsg.ReqCreateTeam.MsgID.eMsgID_VALUE,
                    reqCreateTeamBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);  
        } else {
            super.robotSkipRun();
        }
    }

}
