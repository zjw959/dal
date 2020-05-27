package logic.gift.req;

import java.util.ArrayList;
import java.util.List;

import logic.gift.GiftCodeHelp;
import logic.gift.InvitationCode;
import logic.gift.ReqGiftOrder;
import logic.robot.entity.RobotPlayer;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SLoginMsg;
import org.game.protobuf.s2c.S2CLoginMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.GIFT,
        order = ReqGiftOrder.REQ_GIFT)
public class ReqGiftEvent extends AbstractEvent {

    public ReqGiftEvent(RobotThread robot) {
        super(robot, S2CLoginMsg.GiftCodeRps.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        C2SLoginMsg.GiftCode.Builder build = C2SLoginMsg.GiftCode.newBuilder();
        int randomId = RandomUtils.nextInt(100000);
        String randomStr = "江湖" + randomId;

        List<String> set = new ArrayList<>(GiftCodeHelp.getDefault().ketSet());
        String codeId = null;
        if (!set.isEmpty()) {
            String randomKey = set.get(RandomUtils.nextInt(set.size()));
            InvitationCode code = GiftCodeHelp.getDefault().getCode(randomKey);
            if (code == null) {
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "礼包错误  ");
                super.robotSkipRun();
                return;
            }
            int type = RandomUtils.nextInt(100) % 2 == 0 ? 0 : 1;
            if (type == 0) {
                synchronized (code) {
                    if (code.getGot() != 0) {
                        Log4jManager.getInstance().debug(robot.getWindow(),
                                "robot:" + robot.getName() + "礼包被使用  ");
                        super.robotSkipRun();
                        return;
                    }
                    code.addGot();
                }
                codeId = code.getId();
            } else {
                codeId = randomStr;
            }
        } else {
            codeId = randomStr;
        }

        player.setGiftSendTime(System.currentTimeMillis());
        build.setGiftCode(codeId);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求礼包  " + codeId);
        SMessage msg = new SMessage(C2SLoginMsg.GiftCode.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
