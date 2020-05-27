package com.utils;

import script.ScriptManager;

import com.constant.EScriptIdDefine;
import com.iscript.IAccountControllerScript;
import com.iscript.IAccountPermissionControllerScript;
import com.iscript.IRechargeControllerScript;
import com.iscript.IServerGroupControllerScript;
import com.iscript.IServerManagerControllerScript;

/**
 * 脚本文件调用工具类 所有脚本文件调用接口封装到此工具类下
 */
public class LogicScriptsUtils {

    /**
     * Get IAccountControllerScript instance
     * 
     * @return
     */
    public static IAccountControllerScript getIAccountControllerScript() {
        return (IAccountControllerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.ACCOUNT_CONTROLLER_SCRIPTID.Value());
    }
    
    public static IAccountPermissionControllerScript getIAccountPermissionControllerScript() {
        return (IAccountPermissionControllerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.Account_Permission_Controller_SCRIPTID.Value());
    }
    
    public static IRechargeControllerScript getIRechargeControllerScript() {
        return (IRechargeControllerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.Recharge_Controller_SCRIPTID.Value());
    }
    
    public static IServerGroupControllerScript getIServerGroupControllerScript() {
        return (IServerGroupControllerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.Server_Group_Controller_SCRIPTID.Value());
    }
    
    public static IServerManagerControllerScript getIServerManagerControllerScript() {
        return (IServerManagerControllerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.Server_Manager_Controller_SCRIPTID.Value());
    }
}
