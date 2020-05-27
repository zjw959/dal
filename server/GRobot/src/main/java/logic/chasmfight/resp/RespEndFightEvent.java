package logic.chasmfight.resp;

import org.game.protobuf.s2c.S2CFightMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.kcp.KcpClient;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.channel.Channel;
import logic.chasmfight.unlimited.SendMsgService;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFightMsg.RespEndFight.MsgID.eMsgID_VALUE)
public class RespEndFightEvent extends AbstractEvent {

    public RespEndFightEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            if(robot.isKcp()) {
                KcpClient kcpClient = robot.getKcpClient();
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robotPlayer.getPlayerInfo().getPid() + "断开与战斗服的连接 " + kcpClient.getConv());   
                kcpClient.close();
                RobotThreadFactory.removeFightRobot(String.valueOf(kcpClient.getConv()));
                SendMsgService.unlimitedMsg.remove(String.valueOf(kcpClient.getConv()));
            } else {
                Channel channel = robot.getFightChannel();
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robotPlayer.getPlayerInfo().getPid() + "断开与战斗服的连接 " + channel.id().asLongText());   
                channel.close();
            }
        }
    }

}
