package javascript.logic.item;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.item.IItemScript;
import logic.item.ItemUtils;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;

import utils.CommonUtil;
import cn.hutool.core.map.MapUtil;

/** 掉落类道具脚本 */
public class DropItemScript extends IItemScript {

    @Override
    public boolean isAutoUse(int templateId) {
        return true;
    }

    @Override
    public boolean autoUseNumEnough(Player player, int num, int templateId) {
        return player.getBagManager().enoughByTemplateId(templateId, num);
    }

    @Override
    public boolean doUsebyTemplateId(Player player, int num, int templateId,
            List<Integer> customParam, Map<Integer, Integer> rewardItems, boolean isNotify,
            ItemList.Builder itemMsg) {
        Map<Integer, Integer> drops = MapUtil.newHashMap();
        drops.put(templateId, num);
        Map<Integer, Integer> out =
                ItemUtils.unpackItems(player, null, drops, isNotify, EReason.ITEM_AUTO_USE);// 道具自动解包到背包
        CommonUtil.changeMap(rewardItems, out);
        return true;
    }

    @Override
    public boolean doUsebyUid(Player player, int num, long uid, List<Integer> customParam,
            Map<Integer, Integer> rewardItems, boolean isNotify, ItemList.Builder itemMsg) {
        // 不会用到此方法
        return true;
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ITEM_DROP.Value();
    }
}
