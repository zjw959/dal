package logic.equip.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SEquipmentMsg;
import org.game.protobuf.s2c.S2CEquipmentMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroEquipment;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.equip.ReqEquipOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
        order = ReqEquipOrder.REQ_EQUIP_TAKE_OFF)
public class ReqEquipTakeOffEvent extends AbstractEvent {

    public ReqEquipTakeOffEvent(RobotThread robot) {
        super(robot, S2CEquipmentMsg.TakeOffMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, HeroInfo> heroMap = player.getHeros();
        if (heroMap.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求卸下装备 " + "英雄列表为空");
            super.robotSkipRun();
            return;
        }
        // 英雄
        List<HeroInfo> heroList = new ArrayList<>(heroMap.values());
        List<String> selectInfo = new ArrayList<>();
        for (HeroInfo temp : heroList) {
            for (HeroEquipment equipInfo : temp.getEquipmentsList()) {
                if (!"0".equals(equipInfo.getEquipmentId()) && equipInfo.getPosition() != 0) {
                    String str = temp.getId() + "_" + equipInfo.getEquipmentId() + "_"
                            + equipInfo.getPosition();
                    selectInfo.add(str);
                }
            }
        }
        if (selectInfo.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求卸下装备 " + "无可卸下装备");
            super.robotSkipRun();
            return;
        }

        // 装备
        String value = selectInfo.get(RandomUtils.nextInt(selectInfo.size()));
        String heroId = value.split("_")[0];
        String equipId = value.split("_")[1];
        int pos = Integer.parseInt(value.split("_")[2]);
        C2SEquipmentMsg.TakeOffEquipmentMsg.Builder builder =
                C2SEquipmentMsg.TakeOffEquipmentMsg.newBuilder();
        builder.setEquipmentId(equipId);
        builder.setHeroId(heroId);
        builder.setPosition(pos);
        String str = "{equipId: " + equipId + ", heroId:" + heroId + ", pos:" + pos + "}";
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "卸下装备 " + str);
        SMessage msg = new SMessage(C2SEquipmentMsg.TakeOffEquipmentMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
