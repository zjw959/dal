package javascript.logic.item;

import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.item.IItemScript;
import data.GameDataManager;
import data.bean.BaseGoods;

public class ItemPutTriggerScript extends IItemScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.PUT_TRIGGER.Value();
    }

    @Override
    public void putTrigger(Player player, int templateId, boolean isExist) {
        // 图鉴收集
        if (!isExist) {
            BaseGoods baseGoods = GameDataManager.getBaseGoods(templateId);
            if (baseGoods != null) {
                player.getElementCollectionManager().recordElement(player, baseGoods.getSuperType(),
                        baseGoods.getId(), true);
            }
        }
    }
}
