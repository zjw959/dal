package logic.summon.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.SummonCfgBean;
import data.bean.SummonComposeCfgBean;
import logic.robot.entity.RobotPlayer;
import logic.summon.ReqSummonOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SUMMON,
        order = ReqSummonOrder.REQ_GM_ITEMS)
public class ReqSummonGMItemsEvent extends AbstractEvent {

    public ReqSummonGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.summonId = 0;
        robotPlayer.summonCost = 0;
        robotPlayer.composeId = 0;

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        // 随机抽奖
        List<SummonCfgBean> summonCfgBeans = GameDataManager.getSummonCfgBeans();
        int index = RandomUtils.nextInt(summonCfgBeans.size());
        SummonCfgBean summonCfgBean = summonCfgBeans.get(index);
        List costList = summonCfgBean.getCost();
        int costIndex = RandomUtils.nextInt(costList.size());
        Map<Integer, Integer> costItems = (Map<Integer, Integer>) costList.get(costIndex);

        robotPlayer.summonId = summonCfgBean.getId();
        robotPlayer.summonCost = costIndex;

        for (Map.Entry<Integer, Integer> costItem : costItems.entrySet()) {
            str.append(costItem.getKey());
            str.append(":");
            str.append(costItem.getValue());
            str.append(",");
        }

        // 随机合成
        Map<Integer, Integer> composeInfos = robotPlayer.getComposeInfos();
        Set<Integer> types = composeInfos.keySet();
        List<SummonComposeCfgBean> randomCfgBeans = new ArrayList<>();
        List<SummonComposeCfgBean> summonComposeCfgBeans =
                GameDataManager.getSummonComposeCfgBeans();
        for (SummonComposeCfgBean summonComposeCfgBean : summonComposeCfgBeans) {
            if (!types.contains(summonComposeCfgBean.getZPointType())) {
                randomCfgBeans.add(summonComposeCfgBean);
            }
        }

        if (randomCfgBeans.size() > 0) {
            index = RandomUtils.nextInt(randomCfgBeans.size());
            SummonComposeCfgBean summonComposeCfgBean = randomCfgBeans.get(index);
            costItems = summonComposeCfgBean.getCost();

            robotPlayer.composeId = summonComposeCfgBean.getId();

            for (Map.Entry<Integer, Integer> costItem : costItems.entrySet()) {
                str.append(costItem.getKey());
                str.append(":");
                str.append(costItem.getValue());
                str.append(",");
            }
        }

        String gmContent = str.toString();
        int length = StringUtils.split(gmContent, " ").length;
        if (length > 1) {
            C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
            builder.setChannel(1);
            builder.setFun(1);
            builder.setContent(str.toString());
            SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), this.resOrder);
            sendMsg(msg);
        } else {
            robotPlayer.summonId = 0;
            robotPlayer.summonCost = 0;
            robotPlayer.composeId = 0;
            super.robotSkipRun();
        }
    }

}
