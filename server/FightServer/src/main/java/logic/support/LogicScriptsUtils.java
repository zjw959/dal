package logic.support;

import logic.chasm.IChasmScript;
import logic.constant.EScriptIdDefine;
import redis.IRedisOperScript;
import script.IHttpScript;
import script.ScriptManager;

public class LogicScriptsUtils {
    public static IShutDownScript getShutDownScript() {
        return (IShutDownScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.SHUTDOWN_SCRIPT.Value());
    }
    
    public static IChasmScript getChasmScript() {
        return (IChasmScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CHASM_DUNGEON_SCRIPT.Value());
    }

    public static IHttpScript getIHttpScript() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.HTTPEXECUTE_SCRIPTID.Value());
    }
    
    public static IRedisOperScript getRedisOperScript() {
        return (IRedisOperScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.REDIS_OPERATE_SCRIPT.Value());
    }
}
