package logic.equip;

import java.util.List;

import logic.character.bean.Player;
import logic.hero.bean.Hero;
import logic.item.bean.EquipItem;
import script.IScript;

public abstract class IEquipScript implements IScript {
    protected abstract void changeSpecialAttr(Player player, String eqId, String costId,
            List<Integer> index, List<Integer> costIndex);

    protected abstract void replaceSpecialAttr(Player player, String equipmentId,
            boolean isReplace);

    protected abstract void upgrade(Player player, String sourceEquipment,
            List<String> costEquipmentList);

    protected abstract void takeOff(Player player, Hero hero, int position);

    protected abstract void equip(Player player, Hero hero, EquipItem equipItem, int position);

    protected abstract void lock(Player player, EquipItem equipItem);

}
