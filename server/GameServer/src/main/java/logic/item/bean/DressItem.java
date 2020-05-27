package logic.item.bean;

/**
 * 时装道具
 */
public class DressItem extends BasicItem {
    /**
     * 关联板娘id
     */
    int rid;

    public int getRoleId() {
        return rid;
    }

    public void setRoleId(int rid) {
        this.rid = rid;
    }

    @Override
    public Item copy() {
        DressItem item = new DressItem();
        copyBase(item);
        item.setRoleId(this.rid);
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
        return rid != 0;
    }
}
