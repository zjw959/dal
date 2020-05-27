package javascript.logic.activity;

import logic.activity.script.IActivityReloadLuaScript;
import logic.constant.EScriptIdDefine;

/***
 * 
 * 活动重载lua
 * 
 * @author hongfu.wang
 *
 */

public class ActivityReloadLuaScript implements IActivityReloadLuaScript {


    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITY_RELOAD_LUA_SCRIPT.Value();
    }

    @Override
    public String reloadBuyLua(String script) {
        return script;
    }

    @Override
    public String reloadSelectLua(String script) {
        return script;
    }

    

}
