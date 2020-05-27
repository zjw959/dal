package logic.mail.req;

import logic.mail.ReqMailOrder;
import logic.robot.entity.RobotPlayer;

import org.game.protobuf.c2s.C2SChatMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAIL,
        order = ReqMailOrder.REQ_CREATE_MAILS)
public class ReqGMCreateMailsEvent extends AbstractEvent {

    public ReqGMCreateMailsEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        if (player.getMails().size() > 0) {
            super.robotSkipRun();
            return;
        }
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求GM命令创建邮件  ");
        StringBuilder str = new StringBuilder();
        str.append("./createMail ");
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg =
                new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE, builder.build().toByteArray(),
                        this.resOrder);
        sendMsg(msg, true);
    }

}
