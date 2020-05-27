package logic.summon.resp;

import java.util.List;
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
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.GetComposeInfo.MsgID.eMsgID_VALUE)
public class ResComposeInfoEvent extends AbstractEvent {

    public ResComposeInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            Map<Integer, Integer> composeInfos = robotPlayer.getComposeInfos();
            S2CSummonMsg.GetComposeInfo composeInfoBuilder = S2CSummonMsg.GetComposeInfo.parseFrom(data);
            List<S2CSummonMsg.ComposeInfo> composeInfoTemps = composeInfoBuilder.getComposeInfosList();
            int score = composeInfoBuilder.getScore();
            StringBuilder info = new StringBuilder();
            for(ComposeInfo composeInfo : composeInfoTemps) {
                SummonComposeCfgBean summonComposeCfgBean = GameDataManager.getSummonComposeCfgBean(composeInfo.getCid());
                composeInfos.put(summonComposeCfgBean.getZPointType(), composeInfo.getFinishTime());
                info.append(summonComposeCfgBean.getZPointType());
                info.append(":");
                info.append(composeInfo.getFinishTime());
                info.append(",");
            }
            robotPlayer.setPrayScore(score);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + ",合成信息:" + info.toString());
        }
    }
}
