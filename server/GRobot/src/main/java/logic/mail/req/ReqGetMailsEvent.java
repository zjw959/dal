package logic.mail.req;

import logic.mail.ReqMailOrder;
import logic.robot.entity.RobotPlayer;

import org.game.protobuf.c2s.C2SMailMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAIL,
        order = ReqMailOrder.REQ_GET_MAILS)
public class ReqGetMailsEvent extends AbstractEvent {


    public ReqGetMailsEvent(RobotThread robot) {
        // 不能依赖MailInfoList消息返回再执行下一逻辑，因为收到系统邮件也是返回的同样的消息，会打乱机器人逻辑
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        if (player.getMails().size() > 0) {
            super.robotSkipRun();
            return;
        }
        C2SMailMsg.GetMails.Builder build = C2SMailMsg.GetMails.newBuilder();
        Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "请求邮件  ");
        SMessage msg =
                new SMessage(C2SMailMsg.GetMails.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg, true);
    }

}
