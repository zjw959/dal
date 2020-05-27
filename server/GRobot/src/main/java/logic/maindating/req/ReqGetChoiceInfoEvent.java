package logic.maindating.req;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespChoices;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.BaseFavorDating;
import logic.maindating.ReqMaindatingOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAINDATING,
        order = ReqMaindatingOrder.REQ_CHOICE_INFO)
public class ReqGetChoiceInfoEvent extends AbstractEvent {

    public ReqGetChoiceInfoEvent(RobotThread robot) {
        super(robot, RespChoices.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ReqGetEventChoices.Builder builder = ReqGetEventChoices.newBuilder();
        builder.setDatingType(2);
        builder.setDatingValue(1010101);
        builder.setChoiceType(1);
        int chooseEntranceId = robot.getPlayer().getChooseEntranceId();
        if (chooseEntranceId <= 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "需要前置约会入口选择，无法直接发送信息");
            return;
        }
        int startId = GameDataManager.getFavorScriptCfgBean(chooseEntranceId).getStartId();
        int choiceId = getNextId(startId);
        if (choiceId > 0) {
            builder.setEventId(choiceId);
            robot.getPlayer().choiceId = choiceId;
            SMessage msg = new SMessage(ReqGetEventChoices.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求主线约会选项信息");
        }
    }

    private int getNextId(int startId) {
        BaseFavorDating fdc = GameDataManager.getBaseFavorDating("FavorDating101").get(startId);
        int[] choices = fdc.getJump();
        if (choices.length == 1) {
            startId = getNextId(choices[0]);
        } else if (choices.length > 1) {
            return startId;
        }
        return startId;
    }
}
