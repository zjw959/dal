package logic.support;

import logic.TriggerEvent.ITriggerEventScrip;
import logic.activity.script.IActivityCmdUtilsScript;
import logic.activity.script.IActivityDateProvideScript;
import logic.activity.script.IActivityManagerScript;
import logic.activity.script.IActivityReloadLuaScript;
import logic.activity.script.IActivityStoreHelperScript;
import logic.activity.script.IActivityTaskHelperScript;
import logic.bag.IBagScript;
import logic.character.bean.IPlayerScript;
import logic.chasm.IChasmScript;
import logic.chasm.ITeamScript;
import logic.city.script.ICityInfoManagerScript;
import logic.city.script.ICityRoleManagerScript;
import logic.city.script.ICuisineManagerScript;
import logic.city.script.IManualManagerScript;
import logic.city.script.INewBuildingManagerScript;
import logic.city.script.IPartTimeManagerScript;
import logic.comment.ICommentScript;
import logic.common.ICommonScript;
import logic.common.IFixTimerScript;
import logic.constant.EScriptIdDefine;
import logic.dating.handler.script.IDailyDatingHandlerScript;
import logic.dating.handler.script.IDatingHandlerScript;
import logic.dating.handler.script.IDungeonDatingHandlerScript;
import logic.dating.handler.script.IPhoneDatingHandlerScript;
import logic.dating.handler.script.IReserveDatingHandlerScript;
import logic.dating.handler.script.ITriggerDatingHandlerScript;
import logic.dating.handler.script.ITripDatingHandlerScript;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.IDatingManagerScript;
import logic.dating.script.IDatingServiceScript;
import logic.dating.script.IDatingTriggerScript;
import logic.dating.script.IReserveDatingTriggerScript;
import logic.dating.script.ITripDatingTriggerScript;
import logic.dungeon.script.IBaseSingleDungeonSceneScript;
import logic.dungeon.script.IDungeonCheckScript;
import logic.dungeon.script.IDungeonManagerScript;
import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.elementCollection.IElementCollectionScript;
import logic.endless.IEndlessCloisterScript;
import logic.equip.IEquipScript;
import logic.favor.IFavorDatingScript;
import logic.friend.IFriendScript;
import logic.giftCode.IGiftCodeScript;
import logic.hero.IHeroScript;
import logic.info.IInfoManagerScript;
import logic.item.IItemScript;
import logic.login.service.ILoginCheckScript;
import logic.login.service.ILoginScript;
import logic.mail.IMailScript;
import logic.novelDating.INovelDatingScript;
import logic.pay.IPayScript;
import logic.sign.IApSupplyContainerScript;
import logic.sign.ISevenDaySignManagerScript;
import logic.sign.ISignScript;
import logic.store.IStoreScript;
import logic.summon.ISummonScript;
import logic.task.ITaskScript;
import script.IHttpScript;
import script.ScriptManager;
import server.IShutDownScript;
import thread.db.IDBRoleScript;

/**
 * 脚本文件调用工具类 所有脚本文件调用接口封装到此工具类下
 */
public class LogicScriptsUtils {

    /**
     * Get ICommon Script instance
     * 
     * @return
     */
    public static ICommonScript getICommonScript() {
        return (ICommonScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.COMMON_SCRIPT.Value());
    }

    public static IFixTimerScript getIFixScript() {
        return (IFixTimerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.COMMON_FIX_TIMER_SCRIPT.Value());
    }


    /**
     * Get IHttpScript Instance
     * 
     * @return
     */
    public static IHttpScript getIHttpScript() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.HTTPEXECUTE_SCRIPTID.Value());
    }

    public static IHttpScript getGMHttpScript() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.GM_HTTP_SCRIPTID.Value());
    }

    public static IHttpScript getProgramHttpScript() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.PROGRAM_HTTP_SCRIPTID.Value());
    }

    public static ILoginScript getLoginHttpScript() {
        return (ILoginScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.LOGIN_SCRIPT.Value());
    }

    public static ILoginCheckScript getLoginCheckScript() {
        return (ILoginCheckScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.LOGIN_CHECK_SCRIPT.Value());
    }

    public static IHttpScript getFixBug() {
        return (IHttpScript) ScriptManager.getInstance().getScript(EScriptIdDefine.FIX_BUG.Value());
    }

    public static IHttpScript getFIX_BUG_LOGIC() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.FIX_BUG_LOGIC.Value());
    }

    public static IDBRoleScript getDB_ROLE() {
        return (IDBRoleScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DB_ROLE.Value());
    }

    public static IHttpScript getFIX_BUG_PLAYER() {
        return (IHttpScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.FIX_BUG_PlAYER.Value());
    }

    public static IPayScript getPayScript() {
        return (IPayScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.PAY_SCRIPT.Value());
    }

    public static IMailScript getMailScript() {
        return (IMailScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.MAIL_SCRIPT.Value());
    }

    public static IPlayerScript getPlayerScript() {
        return (IPlayerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.PlAYER.Value());
    }

    public static IGiftCodeScript getGiftCodeScript() {
        return (IGiftCodeScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.GIFT_CODE_SCRIPT.Value());
    }

    public static IShutDownScript getShutDown() {
        return (IShutDownScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.SHUTDOWN_SCRIPT.Value());
    }

    /**
     * Get ILoginScript Instance
     * 
     * @return
     */
    public static ILoginScript getILoginScript() {
        return (ILoginScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.LOGIN_SCRIPT.Value());
    }

    public static IItemScript getIItemScript(int scriptId) {
        return (IItemScript) ScriptManager.getInstance().getScript(scriptId);
    }

    public static IBagScript getIBackpackScript() {
        return (IBagScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.BACKPACK_SCRIPT.Value());
    }

    public static IHeroScript getIHeroScript() {
        return (IHeroScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.HERO_SCRIPT.Value());
    }

    public static IEquipScript getIEquipScript() {
        return (IEquipScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.EQUIP_SCRIPT.Value());
    }

    public static IStoreScript getIStoreScript() {
        return (IStoreScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.STORE_SCRIPT.Value());
    }

    public static ISummonScript getISummonScript() {
        return (ISummonScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.SUMMON_SCRIPT.Value());
    }

    public static ISingleDungeonSceneScript getMainDungeonSceneScript() {
        return (ISingleDungeonSceneScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_MAIN_SCRIPT.Value());
    }

    public static ISingleDungeonSceneScript getGeneralDungeonSceneScript() {
        return (ISingleDungeonSceneScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_GENERAL_SCRIPT.Value());
    }

    public static IDungeonManagerScript getIDungeonManagerScript() {
        return (IDungeonManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_MANAGER_SCRIPT.Value());
    }

    public static ITaskScript getITaskScript() {
        return (ITaskScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.TASK_SCRIPT.Value());
    }

    public static IElementCollectionScript getIElementCollectionScript() {
        return (IElementCollectionScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ELEMENT_COLLECTION_SCRIPT.Value());
    }

    public static ITriggerEventScrip getITriggerEventScript() {
        return (ITriggerEventScrip) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.TRIGGER_EVENT_SCRIPT.Value());
    }

    public static ISingleDungeonSceneScript getActivityDungeonSceneScript() {
        return (ISingleDungeonSceneScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_ACT_SCRIPT.Value());
    }

    public static ISingleDungeonSceneScript getIDungeonDatingScript() {
        return (ISingleDungeonSceneScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_DATING_SCRIPT.Value());
    }

    public static IBaseSingleDungeonSceneScript getIBaseSingleDungeonSceneScript() {
        return (IBaseSingleDungeonSceneScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DUNGEON_BASE_SCRIPT.Value());
    }

    public static ISignScript getITomorrowSignManagerScript() {
        return (ISignScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.TOMORROW_SIGN_SCRIPT.Value());
    }

    public static ISignScript getIMonthSignManagerScript() {
        return (ISignScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.MONTH_SIGN_SCRIPT.Value());
    }
    
    public static ISevenDaySignManagerScript getISevenDaySignManagerScript() {
        return (ISevenDaySignManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.SEVEN_MANAGER_SCRIPT.Value());
    }

    public static ISignScript getIApSupplyManagerScript() {
        return (ISignScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ENERGY_SIGN_SCRIPT.Value());
    }

    public static IApSupplyContainerScript getIApSupplyContainerScript() {
        return (IApSupplyContainerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ENERGY_CONTAINER_SCRIPT.Value());
    }

    public static ITeamScript getTeamScript() {
        return (ITeamScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.TEAM_SCRIPT.Value());
    }

    public static IChasmScript getChasmScript() {
        return (IChasmScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CHASM_DUNGEON_SCRIPT.Value());
    }

    public static IFavorDatingScript getIFavorDatingScript() {
        return (IFavorDatingScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.FAVOR_DATING_SCRIPT.Value());
    }

    public static INovelDatingScript getINovelDatingScript() {
        return (INovelDatingScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.NOVEL_DATING_SCRIPT.Value());
    }


    public static ICityInfoManagerScript getICityInfoManagerScript() {
        return (ICityInfoManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_INFO_SCRIPT.Value());
    }

    public static ICityRoleManagerScript getICityRoleManagerScript() {
        return (ICityRoleManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_ROLE_SCRIPT.Value());
    }

    public static ICuisineManagerScript getICuisineManagerScript() {
        return (ICuisineManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_CUISINE_SCRIPT.Value());
    }

    public static IManualManagerScript getIManualManagerScript() {
        return (IManualManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_MANUAL_SCRIPT.Value());
    }

    public static INewBuildingManagerScript getINewBuildingManagerScript() {
        return (INewBuildingManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_NEWBUILDING_SCRIPT.Value());
    }

    public static IPartTimeManagerScript getIPartTimeManagerScript() {
        return (IPartTimeManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.CITY_PART_JOB_SCRIPT.Value());
    }
    
    public static IEndlessCloisterScript getIEndlessCloisterScript() {
        return (IEndlessCloisterScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ENDLESS_DUNGEON_SCRIPT.Value());
    }

    public static IFriendScript getIFriendScript() {
        return (IFriendScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.FRIEND_SCRIPT.Value());
    }
    public static ICommentScript getICommentScript() {
        return (ICommentScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.COMMENT_SCRIPT.Value());
    }
    
    public static IDatingTriggerScript getIDatingTriggerScript() {
        return (IDatingTriggerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DATING_TRIGGER_SCRIPT.Value());
    }
    public static IBaseDatingScript getIBaseReserveDatingTriggerScript() {
        return (IBaseDatingScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.BASERESERVEDATING_SCRIPT.Value());
    }
    
    public static IBaseDatingScript getIBaseTripDatingTriggerScript() {
        return (IBaseDatingScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.BASETRIPDATING_SCRIPT.Value());
    }
    
    
    public static IReserveDatingTriggerScript getIReserveDatingTriggerScript() {
        return (IReserveDatingTriggerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.RESERVEDATINGTRIGGER_SCRIPT.Value());
    }
    
    public static ITripDatingTriggerScript getITripDatingTriggerScript() {
        return (ITripDatingTriggerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.TRIPDATING_SCRIPT.Value());
    }
    
    public static IDatingManagerScript getIDatingManagerScript() {
        return (IDatingManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DATINGMANAGER_SCRIPT.Value());
    }
    
    public static IDatingServiceScript getIDatingServiceScript() {
        return (IDatingServiceScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.DATINGSERVICE_SCRIPT.Value());
    }
    
    public static IActivityManagerScript getIActivityManagerScript() {
        return (IActivityManagerScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITYMANAGER_SCRIPT.Value());
    }
    
    public static IActivityDateProvideScript getIActivityDateProvideScript() {
        return (IActivityDateProvideScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITYDATEPROVIDE_SCRIPT.Value());
    }
    
    
    public static IActivityCmdUtilsScript getIActivityCmdUtilsScript() {
        return (IActivityCmdUtilsScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITYCMDUTILS_SCRIPT.Value());
    }
    
    public static IActivityReloadLuaScript getIActivityReloadLuaScript() {
        return (IActivityReloadLuaScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITY_RELOAD_LUA_SCRIPT.Value());
    }
    
    
    public static IActivityStoreHelperScript getIActivityStoreHelperScript() {
        return (IActivityStoreHelperScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITY_SHOP_HELPER_SCRIPT.Value());
    }
    
    public static IActivityTaskHelperScript getIActivityTaskHelperScript() {
        return (IActivityTaskHelperScript) ScriptManager.getInstance()
                .getScript(EScriptIdDefine.ACTIVITY_TASK_HELPER_SCRIPT.Value());
    }
    
    public static IInfoManagerScript getIInfoManagerScript() {
        return (IInfoManagerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.INFO_MANAGER_SCRIPT.Value());
    }

    public static IDungeonCheckScript getIDungeonCheckScript() {
        return (IDungeonCheckScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.DUNGEON_CHECK_SCRIPT.Value());
    }
    
    
    public static IDatingHandlerScript getIDatingHandlerScript() {
        return (IDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.DATING_HANDLER_SCRIPT.Value());
    }
    
    public static IDailyDatingHandlerScript getIDaillyHandlerScript() {
        return (IDailyDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.DAILLY_HANDLERHANDLER_SCRIPT.Value());
    }
    
    
    public static IDungeonDatingHandlerScript getIDungeonDatingHandlerScript() {
        return (IDungeonDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.DUNGEONG_HANDLER_SCRIPT.Value());
    }
    
    
    public static IPhoneDatingHandlerScript getIPhoneDatingHandlerScript() {
        return (IPhoneDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.PHONE_HANDLERE_SCRIPT.Value());
    }
    
    public static IReserveDatingHandlerScript getIReserveDatingHandlerScript() {
        return (IReserveDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.RESERVE_HANDLER_SCRIPT.Value());
    }
    
    public static ITriggerDatingHandlerScript getITriggerDatingHandlerScript() {
        return (ITriggerDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.TRIGGER_HANDLER_SCRIPT.Value());
    }
    
    public static ITripDatingHandlerScript getITripDatingHandlerScript() {
        return (ITripDatingHandlerScript) ScriptManager.getInstance().getScript(
                EScriptIdDefine.TTRIP_HANDLER_SCRIPT.Value());
    }
    
    
    
}
