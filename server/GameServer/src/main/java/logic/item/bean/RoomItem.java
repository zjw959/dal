package logic.item.bean;

import com.alibaba.fastjson.JSONObject;

import data.GameDataManager;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;


/**
 * 房间道具
 */
public class RoomItem extends BasicItem {
    /**
     * 关联板娘id
     */
    int rid;

    public int getRoleId() {
        return rid;
    }

    public void setRoleId(int roleId) {
        this.rid = roleId;
    }

    /**
     * 是否默认房间，默认房间不属于任何看板娘
     */
    public boolean isDefaultRoom() {
        return getTemplateId() == Integer.valueOf(GameDataManager
                .getDiscreteDataCfgBean(DiscreteDataID.ROLE).getData()
                .get(DiscreteDataKey.DEFAULT_ROOM).toString());
    }

    @Override
    public Item copy() {
        RoomItem item = new RoomItem();
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
