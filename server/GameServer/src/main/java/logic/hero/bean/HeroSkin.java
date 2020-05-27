package logic.hero.bean;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.item.bean.SkinItem;

public class HeroSkin {
    private long skinId;
    private transient SkinItem skinItem;
    
    public void create(long skinId) {
        this.skinId = skinId;
    }
    
    public void init(Player player) {
        BagManager bagManager = player.getBagManager();
        skinItem = (SkinItem) bagManager.getItemOrigin(skinId);
    }
    
    public void setSkinId(long skinId) {
        this.skinId = skinId;
    }
    
    public long getSkinId() {
        return skinId;
    }
    
    public SkinItem getSkinItem() {
        return skinItem;
    }
    
    public void setSkinItem(SkinItem skinItem) {
        this.skinItem = skinItem;
    }
}
