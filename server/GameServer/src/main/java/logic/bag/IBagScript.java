package logic.bag;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.constant.EReason;
import logic.item.bean.Item;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;

import script.IScript;

public abstract class IBagScript implements IScript {
    /** 提供给脚本的内部方法 **/

    protected void _addNewItemId(BagManager bag, long itemId) {
        bag.addNewItemId(itemId);
    }

    protected boolean _isExist(BagManager bag, int tempId) {
        return bag.isExist(tempId);
    }

    protected Item _getItemOrigin(BagManager bag, long id) {
        return bag.getItemOrigin(id);
    }

    protected Item _putItem(BagManager bag, Item item) {
        return bag.putItem(item);
    }

    protected boolean _enoughByTemplateId(BagManager bag, int templateId, int num) {
        return bag.enoughByTemplateId(templateId, num);
    }
    
    protected boolean _enoughByTemplateId(BagManager bag, Map<Integer,Integer> map) {
        return bag.enoughByTemplateId(map);
    }

    protected boolean _removeByTemplateId(BagManager bag, int templateId, int num,ItemList.Builder builder, List<Item.ItemLogBean> used) {
        return bag.removeByTemplateId(templateId, num, builder, used);
    }

    protected boolean _enoughById(BagManager bag, long id, int num) {
        return bag.enoughById(id, num);
    }
    
    protected boolean _enoughById(BagManager bag, Map<Long,Integer> map) {
        return bag.enoughById(map);
    }

    protected boolean _removeById(BagManager bag, long id, int num, ItemList.Builder itemMsg,
            List<Item.ItemLogBean> used) {
        return bag.removeById(id, num, itemMsg, used);
    }

    /*** 需要实现的方法 **/

    protected abstract List<Item> addItem(BagManager bag, Player player, int templateId, int num,
            boolean isNotify, EReason reason, boolean needMail, String... ext);

    protected abstract List<Item> addItem(BagManager bag, Player player,
            Map<Integer, Integer> templateIdNums, boolean isNotify, EReason reason,
            boolean needMail, String... ext);

    protected abstract boolean removeItem(long id, int num, Player player, BagManager bag,
            boolean isNotify, EReason reason, String... ext);

    protected abstract boolean removeItemsById(BagManager bag, Player player,
            Map<Long, Integer> idNums, boolean isNotify, EReason reason, String... ext);

    protected abstract boolean removeItemByTemplateId(BagManager bag, Player player,
            int templateId, int num, boolean isNotify, EReason reason, String... ext);

    protected abstract boolean removeItemsByTemplateId(BagManager bag, Player player,
            Map<Integer, Integer> tempIdNums, boolean isNotify, EReason reason, boolean needCheck,
            String... ext);

    protected abstract void notifyItemUpdate(Player player, ItemList.Builder itemChange);

    @Deprecated
    protected abstract void removeUnUsdItem(BagManager bagManager, Player player, boolean isNotify,
            EReason reason);
}
