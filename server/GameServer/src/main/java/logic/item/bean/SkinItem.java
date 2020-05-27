package logic.item.bean;

/**
 * 皮肤道具
 */
public class SkinItem extends BasicItem {
    /**
     * 关联精灵id
     */
    int hid;

    public int getHeroId() {
        return hid;
    }

    public void setHeroId(int heroId) {
        this.hid = heroId;
    }

    @Override
    public Item copy() {
        SkinItem item = new SkinItem();
        copyBase(item);
        item.setHeroId(this.hid);
        return item;
    }

    /**
     * NOTE: 构造器私有化只能通过以下方法构造： {@link logic.item.ItemUtils#createItems(int, int)}
     * {@link logic.item.ItemUtils#createItem(JSONObject)}
     */
    @Override
    public void initialize() {

    }

    @Override
    public boolean isInUse() {
        return hid != 0;
    }
}
