package logic.player.name;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.c2s.C2SPlayerMsg.SetPlayerInfo.Builder;
import org.game.protobuf.s2c.S2CPlayerMsg.setPlayerInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
        order = ReqOnceOrder.REQ_CHANGE_NAME)
public class ReqChangeNameEvent extends AbstractEvent {

    public ReqChangeNameEvent(RobotThread robot) {
        super(robot, setPlayerInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        Builder build = C2SPlayerMsg.SetPlayerInfo.newBuilder();
        build.setPlayerName(robot.getName());
        build.setRemark("my name is :" + robot.getName() + ",my login from :" + _getLocalIP());
        SMessage msg = new SMessage(C2SPlayerMsg.SetPlayerInfo.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

    private String _getLocalIP() throws SocketException {
        String ip = "";
        // 通过网卡获取
        Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
        while (e1.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e1.nextElement();
            Enumeration<?> e2 = ni.getInetAddresses();
            while (e2.hasMoreElements()) {
                InetAddress ia = (InetAddress) e2.nextElement();
                if (ia instanceof Inet6Address)
                    continue;
                String address = ia.getHostAddress();
                if (address.startsWith("192.168.") || address.startsWith("10.")) {
                    ip = address;
                    return ip;
                }
            }
        }
        return ip;
    }
}
