package logic.player;

import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = PlayerInfo.MsgID.eMsgID_VALUE)
public class ResPlayerInfoEvent extends AbstractEvent {

    public ResPlayerInfoEvent(RobotThread robot) {
        super(robot);
        this.resOrder = PlayerInfo.MsgID.eMsgID_VALUE;
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            PlayerInfo playerInfo = PlayerInfo.parseFrom(data);
            RobotPlayer robotPlayer = robot.getPlayer();
            PlayerInfo.Builder playerInfoBuilder = PlayerInfo.newBuilder(robotPlayer.getPlayerInfo());
            playerInfoBuilder.setName(playerInfo.getName());
            playerInfoBuilder.setRemark(playerInfo.getRemark());
            robotPlayer.setPlayerInfo(playerInfoBuilder.build());
        }
    }

}
