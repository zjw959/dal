package logic.msgBuilder;



import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CEquipmentMsg;
import org.game.protobuf.s2c.S2CItemMsg;
import org.game.protobuf.s2c.S2CShareMsg;

import logic.hero.bean.Hero;
import logic.item.bean.EquipItem;
import logic.task.handler.MReqTasksHandler;

/**
 * 灵装消息封装
 */
public class EquipmentMsgBuilder {
    private static final Logger LOGGER = Logger.getLogger(MReqTasksHandler.class);

    public static S2CEquipmentMsg.EquipMsg getEquipMsg(EquipItem equipment,
            EquipItem oldEquipment) {
        S2CEquipmentMsg.EquipMsg.Builder builder = S2CEquipmentMsg.EquipMsg.newBuilder();
        builder.setEquipment(getPartEquipmentInfo(equipment));
        if (oldEquipment != null) {
            builder.setOldEquipment(getPartEquipmentInfo(oldEquipment));
        }
        return builder.build();
    }

    public static S2CEquipmentMsg.TakeOffMsg getTakeOffMsg(EquipItem equipment, Hero hero) {
        S2CEquipmentMsg.TakeOffMsg.Builder builder = S2CEquipmentMsg.TakeOffMsg.newBuilder();
        builder.setSuccess(true);
        return builder.build();
    }

    public static S2CEquipmentMsg.ChangeSpecialAttrMsg getChangeSpecialAttrMsg(boolean isSuccess) {
        S2CEquipmentMsg.ChangeSpecialAttrMsg.Builder builder =
                S2CEquipmentMsg.ChangeSpecialAttrMsg.newBuilder();
        builder.setSuccess(isSuccess);
        return builder.build();
    }

    public static S2CEquipmentMsg.UpgradeMsg getUpgradeMsg() {
        S2CEquipmentMsg.UpgradeMsg.Builder builder = S2CEquipmentMsg.UpgradeMsg.newBuilder();
        builder.setSuccess(true);
        return builder.build();
    }

    public static S2CEquipmentMsg.ReplaceSpecialAttrMsg getReplaceSpecialAttrMsg() {
        S2CEquipmentMsg.ReplaceSpecialAttrMsg.Builder builder =
                S2CEquipmentMsg.ReplaceSpecialAttrMsg.newBuilder();
        builder.setSuccess(true);
        return builder.build();
    }

    /**
     * 封装部分装备信息 仅更新变化信息
     */
    private static S2CItemMsg.EquipmentInfo getPartEquipmentInfo(EquipItem equipment) {
        S2CItemMsg.EquipmentInfo.Builder equipmentInfoBuilder =
                S2CItemMsg.EquipmentInfo.newBuilder();

        equipmentInfoBuilder.setCt(S2CShareMsg.ChangeType.UPDATE)
                .setId(Long.toString(equipment.getId())).setPosition(equipment.getPosition())
                .setHeroId(Long.toString(equipment.getHeroId()));
        return equipmentInfoBuilder.build();
    }


}
