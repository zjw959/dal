package logic.equip.req;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SEquipmentMsg;
import org.game.protobuf.s2c.S2CEquipmentMsg;
import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.EquipmentGrowthCfgBean;
import logic.constant.ItemConstantId;
import logic.equip.ReqEquipOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
        order = ReqEquipOrder.REQ_UPGRADE)
public class ReqEquipUpgradeEvent extends AbstractEvent {

    public ReqEquipUpgradeEvent(RobotThread robot) {
        super(robot, S2CEquipmentMsg.UpgradeMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<String, EquipmentInfo> eqMap = player.getEquipmentKV();
        if (eqMap == null || eqMap.size() <= 1) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "装备强化  " + "装备数量小于1");
            super.robotSkipRun();
            return;
        }

        List<String> set = new ArrayList<>(eqMap.keySet());
        String randomStr = set.get(RandomUtils.nextInt(set.size()));
        EquipmentInfo info = eqMap.get(randomStr);
        // int size = RandomUtils.nextInt(eqMap.size());
        EquipmentCfgBean config = GameDataManager.getEquipmentCfgBean(info.getCid());
        if (config == null) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "装备强化  " + "config is null");
            super.robotSkipRun();
            return;
        }

        int size = 1;
        List<String> keyList = new ArrayList<>();
        DiscreteDataCfgBean coumse = GameDataManager.getDiscreteDataCfgBean(4002);
        Object object = coumse.getData().get("ratio");
        int costRatio = Integer.parseInt(object.toString());
        int costGold = 0;
        for (int i = 0; i < size; i++) {
            String _randomKey = set.get(RandomUtils.nextInt(set.size()));
            EquipmentInfo randomInfo = eqMap.get(_randomKey);
            if (!_randomKey.equals(info.getId()) && randomInfo.getHeroId().equals("0")
                    && randomInfo.getPosition() == 0) {
                keyList.add(_randomKey);
                int exp = getEquipmentExp(eqMap.get(_randomKey));
                costGold += exp * costRatio / 10000;
            }
        }
        Map<Integer, Integer> map = new HashMap<>();
        map.put(ItemConstantId.GOLD, costGold);
        if (keyList.isEmpty() || !player.isEnoughItem(map)) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "装备强化  " + "没有消耗道具");
            super.robotSkipRun();
            return;
        }

        int star = config.getStar();
        EquipmentGrowthCfgBean growthCfg = GameDataManager.getEquipmentGrowthCfgBean(star);
        int[] expList = growthCfg.getNeedExp();
        if (info.getLevel() >= expList.length) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "装备强化  " + "已达到满级");
            super.robotSkipRun();
            return;
        }



        String str = "{equipId: " + info.getId() + ", size:" + keyList.size() + "}";
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "装备强化  " + str);
        C2SEquipmentMsg.UpgradeMsg.Builder builder = C2SEquipmentMsg.UpgradeMsg.newBuilder();
        builder.setEquipmentId(info.getId());
        builder.addAllCostEquipmentId(keyList);
        SMessage msg = new SMessage(C2SEquipmentMsg.UpgradeMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

    /**
     * 计算被消耗的灵装提供的经验
     */
    private int getEquipmentExp(EquipmentInfo equipment) {
        EquipmentGrowthCfgBean growthCfg = GameDataManager.getEquipmentGrowthCfgBean(1);
        int growthExp = 0;
        if (equipment.getLevel() > 1) {

            for (int i = 0; i < growthCfg.getNeedExp().length; i++) {
                if (i >= equipment.getLevel() - 1)
                    break;
                growthExp += (int) growthCfg.getNeedExp()[i];
            }
        }
        int exp = GameDataManager.getEquipmentCfgBean(equipment.getCid()).getExp();
        return exp + growthExp;
    }

}
