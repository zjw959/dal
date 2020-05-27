package logic.item;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;

import script.IScript;

public abstract class IItemScript implements IScript {
    /**
     * 入背包触发事件
     * 
     * @param templateId
     * @param isNew
     * @return
     */
    public void putTrigger(Player player, int templateId, boolean isExist) {};

    /** 使用前提 **/
    public boolean usePremise(Player player, int templateId) {
        return true;
    }

    /** 获取道具数量 **/
    public int getItemCount(Player player, int templateId) {
        return player.getBagManager().getItemCount(templateId);
    }

    /**
     * 是否是自使用道具
     * 
     * @param templateId
     * @return
     */
    public boolean isAutoUse(int templateId) {
        return false;
    }

    /**
     * 自使用的具体使用(数量)前提判断
     **/
    public boolean autoUseNumEnough(Player player, int num, int templateId) {
        return false;
    }

    /**
     * 使用道具
     * 
     * @param player
     * @param num
     * @param templateId
     * @param customParam 参数
     * @param rewardItems 获得的新的道具
     * @param isNotify
     * @param itemMsg
     * @return
     */
    public boolean doUsebyTemplateId(Player player, int num, int templateId,
            List<Integer> customParam, Map<Integer, Integer> rewardItems, boolean isNotify,
            ItemList.Builder itemMsg) {
        return true;
    };
    
    /**
     * 使用道具
     * 
     * @param player
     * @param num
     * @param uid
     * @param customParam 参数
     * @param rewardItems 获得的新的道具
     * @param isNotify
     * @param itemMsg
     * @return
     */
    public boolean doUsebyUid(Player player, int num, long uid, List<Integer> customParam,
            Map<Integer, Integer> rewardItems, boolean isNotify, ItemList.Builder itemMsg) {
        return true;
    };
}
