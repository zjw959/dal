package logic.summon.resp;

import java.util.Map;

import org.game.protobuf.s2c.S2CSummonMsg;
import org.game.protobuf.s2c.S2CSummonMsg.ComposeInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.SummonComposeCfgBean;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.ComposeSummon.MsgID.eMsgID_VALUE)
public class ResComposeSummonEvent extends AbstractEvent {

    public ResComposeSummonEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CSummonMsg.ComposeSummon composeSummonBuilder =
                    S2CSummonMsg.ComposeSummon.parseFrom(data);
            ComposeInfo composeInfo = composeSummonBuilder.getComposeInfo();
            int cid = composeInfo.getCid();
            int finishTime = composeInfo.getFinishTime();
            SummonComposeCfgBean summonComposeCfgBean = GameDataManager.getSummonComposeCfgBean(cid);
            Map<Integer, Integer> composeInfos = robot.getPlayer().getComposeInfos();
            composeInfos.put(summonComposeCfgBean.getZPointType(), finishTime);

            StringBuilder info = new StringBuilder();
            info.append(summonComposeCfgBean.getZPointType());
            info.append(":");
            info.append(finishTime);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + ",合成:" + info.toString());
        }
    }
}
