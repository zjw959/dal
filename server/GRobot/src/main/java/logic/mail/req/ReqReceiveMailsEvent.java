package logic.mail.req;

import logic.mail.ReqMailOrder;
import logic.robot.entity.RobotPlayer;

import org.game.protobuf.c2s.C2SMailMsg;
import org.game.protobuf.c2s.C2SMailMsg.MailOperationType;
import org.game.protobuf.s2c.S2CMailMsg.MailInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAIL,
        order = ReqMailOrder.REQ_RECEIVE_MAILS)
public class ReqReceiveMailsEvent extends AbstractEvent {

    public ReqReceiveMailsEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SMailMsg.MailHandleMsg.Builder build = C2SMailMsg.MailHandleMsg.newBuilder();
        build.setType(MailOperationType.RECEIVE);
        RobotPlayer player = robot.getPlayer();
        if (player.getMails().size() == 0) {
            super.robotSkipRun();
            return;
        }
        int max = 10;// 一次操作数量限制
        for (MailInfo mail : player.getMails().values()) {
            if (mail.getStatus() == 1 && mail.getRewardsCount() > 0) {// 已读且有附件的邮件
                build.addIds(mail.getId());
                if (build.getIdsCount() >= max) {
                    break;
                }
            }
        }
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求领取邮件附件  ");
        SMessage msg = new SMessage(C2SMailMsg.MailHandleMsg.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg, true);
    }

}
