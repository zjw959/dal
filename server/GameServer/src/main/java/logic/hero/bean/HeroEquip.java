package logic.hero.bean;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.item.bean.EquipItem;

public class HeroEquip {
    private long equipId;
    private transient EquipItem equipItem;

    public void create(long equipId) {
        this.equipId = equipId;
    }

    public void init(Player player) {
        BagManager bagManager = player.getBagManager();
        equipItem = (EquipItem) bagManager.getItemOrigin(equipId);
    }

    public long getEquipId() {
        return equipId;
    }

    public void setEquipId(long equipId) {
        this.equipId = equipId;
    }

    public EquipItem getEquipItem() {
        return equipItem;
    }

    public void setEquipItem(EquipItem equipItem) {
        this.equipItem = equipItem;
    }

}
