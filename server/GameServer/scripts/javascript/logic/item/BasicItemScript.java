package javascript.logic.item;

import logic.constant.EScriptIdDefine;
import logic.item.IItemScript;

public class BasicItemScript extends IItemScript {
    @Override
    public int getScriptId() {
        return EScriptIdDefine.ITEM_BASIC.Value();
    }
}
