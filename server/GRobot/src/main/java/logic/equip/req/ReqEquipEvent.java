package logic.equip.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SEquipmentMsg;
import org.game.protobuf.s2c.S2CEquipmentMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroEquipment;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;
import org.game.protobuf.s2c.S2CShareMsg.AttributeInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.EquipmentCfgBean;
import logic.equip.ReqEquipOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
        order = ReqEquipOrder.REQ_EQUIP)
public class ReqEquipEvent extends AbstractEvent {

    public ReqEquipEvent(RobotThread robot) {
        super(robot, S2CEquipmentMsg.EquipMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, HeroInfo> heroMap = player.getHeros();
        Map<String, EquipmentInfo> eqMap = player.getEquipmentKV();
        if (heroMap.isEmpty() || eqMap.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求装备  " + "英雄列表或者装备列表为空");
            super.robotSkipRun();
            return;
        }
        // 英雄
        List<HeroInfo> heroList = new ArrayList<>(heroMap.values());
        HeroInfo hero = heroList.get(RandomUtils.nextInt(heroMap.size()));
        int pos = RandomUtils.nextInt(3) + 1;
        // 装备
        List<String> set = new ArrayList<>(eqMap.keySet());
        String randomKey = set.get(RandomUtils.nextInt(set.size()));
        EquipmentInfo info = eqMap.get(randomKey);
        EquipmentInfo oldEquipment = null;
        for (HeroEquipment heroEquip : hero.getEquipmentsList()) {
            EquipmentInfo _info = eqMap.get(heroEquip.getEquipmentId());
            if (info.getId().equals(_info.getId()) && info.getHeroId().equals(_info.getHeroId())
                    && info.getPosition() == _info.getPosition()) {
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "请求装备  " + "无法操作同一件装备");
                super.robotSkipRun();
                return;
            }
            if (_info.getPosition() == pos) {
                oldEquipment = _info;
            }
        }
        boolean code = checkHeroCost(player, hero, info, oldEquipment);
        if (code) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求装备  " + "装备过载");
            super.robotSkipRun();
            return;
        }

        C2SEquipmentMsg.EquipMsg.Builder build = C2SEquipmentMsg.EquipMsg.newBuilder();
        build.setEquipmentId(info.getId());
        build.setHeroId(hero.getId());
        build.setPosition(pos);
        String str =
                "{equipId: " + info.getId() + ", heroId:" + hero.getId() + ", pos:" + pos + "}";
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求装备  " + str);
        SMessage msg = new SMessage(C2SEquipmentMsg.EquipMsg.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

    /**
     * 检查hero负载
     * 
     * @param hero 英雄
     * @param newEquipment 新装备
     * @param oldEquipment 旧装备
     */
    public boolean checkHeroCost(RobotPlayer player, HeroInfo hero, EquipmentInfo newEquipment,
            EquipmentInfo oldEquipment) {
        int totalCost = 0;
        for (AttributeInfo info : hero.getAttrList()) {
            if (info.getType() == 10) {
                totalCost = info.getVal();
                break;
            }
        }
        EquipmentCfgBean cfgBean = GameDataManager.getEquipmentCfgBean(newEquipment.getCid());
        int afterCost = cfgBean.getCost();

        for (HeroEquipment heroEquip : hero.getEquipmentsList()) {
            if (oldEquipment != null && heroEquip.getEquipmentId().equals(oldEquipment.getId())) {
                continue;
            }
            EquipmentInfo info = player.getEquipmentKV().get(heroEquip.getEquipmentId());
            cfgBean = GameDataManager.getEquipmentCfgBean(info.getCid());
            afterCost += cfgBean.getCost();
        }
        return afterCost > totalCost;
    }
}
