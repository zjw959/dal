package logic.login.req;

import org.apache.http.client.config.RequestConfig;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SLoginMsg;
import org.game.protobuf.s2c.S2CLoginMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;


@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
        order = ReqOnceOrder.REQ_LOGIN)
public class ReqLoginEvent extends AbstractEvent {

    private static final Logger LOGGER = Logger.getLogger(ReqLoginEvent.class);
    // 去登录服拿验证token
    // public String url = "http://192.168.20.182:8081/account/login";

    public ReqLoginEvent(RobotThread robot) {
        super(robot);
        this.resOrder = S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE;
    }

    @Override
    public void action(Object... obj) throws Exception {
        String token = robot.token;
        if (token == null || token.length() <= 0) {
            LOGGER.error("token is empty!");
            return;
        }
        C2SLoginMsg.EnterGame.Builder enBuilder = C2SLoginMsg.EnterGame.newBuilder();
        enBuilder.setToken(token);
        SMessage msg = new SMessage(C2SLoginMsg.EnterGame.MsgID.eMsgID_VALUE,
                enBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }
}
