package logic.chasmfight.resp;

import java.net.InetSocketAddress;

import org.game.protobuf.s2c.S2CChasmMsg;
import org.game.protobuf.s2c.S2CPlayerMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.connect.GClient;
import core.net.kcp.KcpClient;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import logic.robot.entity.RobotPlayer;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CChasmMsg.RsepChasmStartFight.MsgID.eMsgID_VALUE)
public class RespChasmStartFightEvent extends AbstractEvent {

    public RespChasmStartFightEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
            S2CChasmMsg.RsepChasmStartFight respChasmStartFight =
                    S2CChasmMsg.RsepChasmStartFight.parseFrom(data);
            String fightServerHost = respChasmStartFight.getFightServerHost();
            int fightServerPort = respChasmStartFight.getFightServerPort();
            robotPlayer.fightId = respChasmStartFight.getFightId();
            robotPlayer.randomSeed = respChasmStartFight.getRandomSeed();
            int netType = respChasmStartFight.getNetType();
            if (netType == 1) {
                robot.setKcp(true);
                KcpClient kcpClient = new KcpClient();
                kcpClient.noDelay(1, 20, 2, 1);
                kcpClient.setMinRto(10);
                kcpClient.wndSize(128, 128);
                kcpClient.setTimeout(20 * 1000);
                kcpClient.setMtu(512);
                kcpClient.setConv(playerInfo.getPid());
                kcpClient.connect(new InetSocketAddress(fightServerHost, fightServerPort));
                kcpClient.start();
                robot.setKcpClient(kcpClient);
                RobotThreadFactory.putFightRobot(String.valueOf(playerInfo.getPid()), robot);
                KcpOnUdp kcpOnUdp = robot.getKcpClient().getKcpOnUdp();
                kcpOnUdp.setSessionValue(KcpConstant.ENCRYPTION_KEYS,
                        EncryptionAndDecryptionUtil.getDefaultCustomEncryptionKeys());
                kcpOnUdp.setSessionValue(KcpConstant.DECRYPTION_KEYS,
                        EncryptionAndDecryptionUtil.getDefaultCustomDecryptionKeys());
            } else {
                robot.setKcp(false);
                ChannelFuture channelFuture = GClient.connect(fightServerHost, fightServerPort);
                if (channelFuture != null) {
                    // 等待连接，不能在netty的hanler创建连接时调用await()或sync()会产生死锁
                    Thread.sleep(10);
                    Channel channel = channelFuture.channel();
                    robot.setFightChannel(channel);
                    RobotThreadFactory.putFightRobot(channel.id().asLongText(), robot);
                } else {
                    Log4jManager.getInstance().error(robot.getWindow(),
                            "robot:" + playerInfo.getPid() + "连接战斗服失败ip:" + fightServerHost
                                    + ",port:" + fightServerPort);
                }
            }
        }
    }
}
