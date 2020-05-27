package logic.activity.script;

import script.IScript;

/**
 * 活动重载lua
 * 
 * @author hongfu.wang
 *
 */
public interface IActivityReloadLuaScript extends IScript {

    String reloadBuyLua(String luaScript_buy);

    String reloadSelectLua(String luaScript_select);

}
