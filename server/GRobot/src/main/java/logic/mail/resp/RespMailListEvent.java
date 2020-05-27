package logic.mail.resp;

import java.util.List;
import java.util.Map;

import logic.robot.entity.RobotPlayer;

import org.game.protobuf.s2c.S2CMailMsg;
import org.game.protobuf.s2c.S2CMailMsg.MailInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CMailMsg.MailInfoList.MsgID.eMsgID_VALUE)
public class RespMailListEvent extends AbstractEvent {

    public RespMailListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            Map<String, MailInfo> map = player.getMails();
            S2CMailMsg.MailInfoList msg = S2CMailMsg.MailInfoList.parseFrom(data);
            List<MailInfo> mails = msg.getMailsList();
            ChangeType type = ChangeType.DEFAULT;
            for (MailInfo mail : mails) {
                type = mail.getCt();
                if (type == ChangeType.DEFAULT||type == ChangeType.ADD||type == ChangeType.UPDATE) {
                    map.put(mail.getId(), mail);
                } else if (type == ChangeType.DELETE) {
                    map.remove(mail.getId());
                }
            }
            String str =
                    "{type:" + type + " mailCount: " + msg.getMailsCount() + " totalCount:"
                            + map.size() + "}";
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "响应邮件事件  " + str);
        }
    }

}
