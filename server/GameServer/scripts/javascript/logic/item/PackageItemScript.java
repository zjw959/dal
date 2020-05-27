package javascript.logic.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.item.IItemScript;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.support.MessageUtils;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;

import utils.CommonUtil;
import utils.ToolMap;
import cn.hutool.core.map.MapUtil;
import data.GameDataManager;
import data.bean.BaseGoods;

public class PackageItemScript extends IItemScript {

    @Override
    public boolean isAutoUse(int templateId) {
        return false;
    }

    @Override
    public boolean autoUseNumEnough(Player player, int num, int templateId) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doUsebyTemplateId(Player player, int num, int templateId,
            List<Integer> customParam, Map<Integer, Integer> rewardItems, boolean isNotify,
            ItemList.Builder itemMsg) {
        BagManager bagM = player.getBagManager();
        if (!bagM.enoughByTemplateId(templateId, num)) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "item is not enough");
        }
        // 使用消耗
        Map<Integer, Integer> useCast = new HashMap<Integer, Integer>();
        BaseGoods baseGoods = GameDataManager.getBaseGoods(templateId);
        Map<Integer, Integer> itemUseCast = baseGoods.getUseCast();
        if (itemUseCast != null && !itemUseCast.isEmpty()) {
            for (Entry<Integer, Integer> e : itemUseCast.entrySet()) {
                int castCount = ToolMap.getInt(e.getKey(), useCast, 0) + e.getValue();
                useCast.put(e.getKey(), castCount);
            }
        }
        if (!useCast.isEmpty()) {
            // 检查使用消耗道具是否足够
            if (!bagM.enoughByTemplateId(useCast)) {
                MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH,
                        "cost items is not enough");
            }
            // 扣除使用消耗
            bagM.removeItemsByTemplateIdNoCheck(useCast, isNotify, EReason.ITEM_USE);
        }
        if (bagM.removeItemByTempId(templateId, num, isNotify, EReason.ITEM_USE)) {
            // 解包礼包道具
            Map<Integer, Integer> in = MapUtil.newHashMap();
            in.put(templateId, num);
            Map<Integer, Integer> out =
                    ItemUtils.unpackItems(player, customParam, in, isNotify, EReason.ITEM_USE);
            for (Entry<Integer, Integer> entry : out.entrySet()) {
                CommonUtil.changeMap(rewardItems, entry.getKey(), entry.getValue());
            }
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doUsebyUid(Player player, int num, long uid, List<Integer> customParam,
            Map<Integer, Integer> rewardItems, boolean isNotify, ItemList.Builder itemMsg) {
        BagManager bagM = player.getBagManager();
        Item item = bagM.getItemCopy(uid);
        if (item == null || item.getNum() < num) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "item is not enough");
        }
        // 使用消耗
        Map<Integer, Integer> useCast = new HashMap<Integer, Integer>();
        BaseGoods baseGoods = GameDataManager.getBaseGoods(item.getTemplateId());
        Map<Integer, Integer> itemUseCast = baseGoods.getUseCast();
        if (itemUseCast != null && !itemUseCast.isEmpty()) {
            for (Entry<Integer, Integer> e : itemUseCast.entrySet()) {
                int castCount = ToolMap.getInt(e.getKey(), useCast, 0) + e.getValue();
                useCast.put(e.getKey(), castCount);
            }
        }
        if (!useCast.isEmpty()) {
            // 检查使用消耗道具是否足够
            if (!bagM.enoughByTemplateId(useCast)) {
                MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH,
                        "cost items is not enough");
            }
            // 扣除使用消耗
            bagM.removeItemsByTemplateIdNoCheck(useCast, isNotify, EReason.ITEM_USE);
        }
        Map<Long, Integer> idNums = MapUtil.newHashMap();
        idNums.put(uid, num);
        if (bagM.removeItemsByIds(idNums, isNotify, EReason.ITEM_USE)) {
            // 解包礼包道具
            Map<Integer, Integer> in = MapUtil.newHashMap();
            in.put(item.getTemplateId(), num);
            Map<Integer, Integer> out =
                    ItemUtils.unpackItems(player, customParam, in, isNotify, EReason.ITEM_USE);
            CommonUtil.changeMap(rewardItems, out);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ITEM_PACKAGE.Value();
    }
}
