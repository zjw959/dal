package logic.item.bean;

import com.alibaba.fastjson.JSONObject;


/**
 * 普通道具
 */
public class BasicItem extends Item {
    
    @Override
    public Item copy() {
        BasicItem item = new BasicItem();
        copyBase(item);
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
        return false;
    }

//    @Override
//    public void buildItemOtherData(Message.ItemMsg.ItemData.Builder builder) {
//
//    }
}
