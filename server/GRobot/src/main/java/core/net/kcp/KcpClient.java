package core.net.kcp;

import java.util.List;

import org.apache.log4j.Logger;

import core.net.message.SMessage;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.buffer.ByteBuf;
import net.kcp.BaseKcpClient;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;
import utils.ExceptionEx;

public class KcpClient extends BaseKcpClient {
    private static final Logger LOGGER = Logger.getLogger(KcpClient.class);

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        List<SMessage> msgs =
                KcpCodec.decoder(bb, (int[]) kcp.getSessionValue(KcpConstant.DECRYPTION_KEYS));
        RobotThread robot = RobotThreadFactory.getFightRobot(String.valueOf(kcp.getKcp().getConv()));
        for (SMessage msg : msgs) {
            robot.getWindow().addReceiveMsgs();
            robot.addRespMsg(msg);
        }
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        super.handleClose(kcp);
        LOGGER.error(kcp.getKcp().getConv() + " " + ExceptionEx.t2s(ex));
    }

}
