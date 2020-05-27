package logic.activity.helper.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;

import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityShopItem;
import logic.activity.bean.ActivityRecord;
import logic.activity.helper.DefaultActivityExecutor;
import logic.activity.script.IActivityStoreHelperScript;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import redis.base.RedisCmd;

/***
 * 
 * @author 商品活动helper
 *
 */
public class ActivityStoreHelper extends DefaultActivityExecutor {

    public static final Logger LOGGER = Logger.getLogger(ActivityStoreHelper.class);

    /** 不限购 */
    public static final int LIMIT_NO_TYPE = 0;
    /** 每日限购 */
    public static final int LIMIT_TO_DAY_TYPE = 1;
    /** 总数限购 */
    public static final int LIMIT_TOTAL_TYPE = 2;
    /** 全服每日限购 */
    public static final int LIMIT_TO_ALL_SERVER_DAY_TYPE = 3;
    /** 全服总数限购 */
    public static final int LIMIT_ALL_SERVER_TOTAL_TYPE = 4;

    /**
     * <活动id,<商品条目id，商品信息>>
     */
    private Map<Integer, Map<Integer, ActivityShopItem>> map =
            new HashMap<Integer, Map<Integer, ActivityShopItem>>();


    private IActivityStoreHelperScript getManagerScript() {
        return LogicScriptsUtils.getIActivityStoreHelperScript();
    }



    /** 开启活动 **/
    @Override
    public void openActivity(ActivityConfigure config) {
        getManagerScript().openActivity(config, map, LOGGER);
    }

    /** 查询条目 **/
    private List<ActivityShopItem> queryItemList(ActivityConfigure config) {
        return getManagerScript().queryItemList(config);
    }

    /** 关闭活动 **/
    @Override
    public void closeActivity(ActivityConfigure config) {
        getManagerScript().closeActivity(config, map);
    }

    /** 修改活动 **/
    @Override
    public void changeActivity(ActivityConfigure config) {
        getManagerScript().changeActivity(config, map, LOGGER);
    }

    /** 组装数据 **/
    @Override
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder itemBuild,
            Integer itemId, ActivityConfigure config, Player player) {
        return getManagerScript().getActivityEntryToBuild(itemBuild, itemId, config, player, map);
    }

    /** 商店活动 判断是否还能领奖（购买） --就是判断限购次数和个人购买次数 
     * @throws Exception **/
    @Override
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData) throws Exception {
        return getManagerScript().isGotReward(config, record, itemId, extendData, map, LOGGER);
    }

    /**
     * 生成商品的主键id
     * 
     * @param config
     * @param item
     * @return
     */
    public String generateKey(ActivityConfigure config, ActivityShopItem item) {
        return getManagerScript().generateKey(config, item);
    }

    public String generateTimeKey(ActivityConfigure config, ActivityShopItem item) {
        return getManagerScript().generateTimeKey(config, item);
    }

    // 判断玩家身上的资源是否足够
    @Override
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData) {
        return getManagerScript().isCanReward(player, config, itemId, extendData, map, LOGGER);
    }


    /** 领取活动奖励 成功领取ture 
     * @throws Exception **/
    @Override
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record) throws Exception {
        return getManagerScript().getReward(player, config, itemId, extendData, record, map,
                LOGGER);
    }

    private static String luaScript_buy = "local current = redis.call('GET', KEYS[1])  "
            + "if tonumber(current) == nil then" + "    redis.call('SET', KEYS[1],0)"
            + "    current = redis.call('GET', KEYS[1]) " + "end "
            + "if tonumber(current)+tonumber(ARGV[2])>tonumber(ARGV[1]) then " + "    return 0  "
            + "else " + "    current = redis.call('INCRBY', KEYS[1], ARGV[2]) "
            + "    return tonumber(current) " + "end ";

    private static String luaScript_select = "local current = redis.call('GET', KEYS[1])  "
            + "local refreshTime = redis.call('GET' , KEYS[2])  " + "local nowTime = ARGV[1] "
            + "local nextTime = ARGV[2] "
            + "if tonumber(current) == nil or tonumber(refreshTime)==nil then "
            + "       redis.call('SET', KEYS[1],'0') "
            + "       redis.call('SET', KEYS[2],nextTime) "
            + "       current = redis.call('GET', KEYS[1]) "
            + "       refreshTime = redis.call('GET' , KEYS[2]) " + "end "
            + "if tonumber(nextTime)>tonumber(nowTime) then " + "       return tonumber(current) "
            + "else " + "       redis.call('SET', KEYS[1],'0') "
            + "       redis.call('SET', KEYS[2],nextTime) " + "       return tonumber(0) " + "end";
    public static String buy_sha = "";
    public static String sel_sha = "";

    public static void _scriptLoad_Buy(redis.base.RedisOper redisOper, String key)
            throws Exception {
        String _sha1 = (String) redisOper.execute(RedisCmd.scriptload, key, luaScript_buy);
        if (_sha1 == null || _sha1.isEmpty()) {
            throw new Exception("can not load script");
        }
        buy_sha = _sha1;
    }

    public static void _scriptLoad_Select(redis.base.RedisOper redisOper, String key)
            throws Exception {
        String _sha1 = (String) redisOper.execute(RedisCmd.scriptload, key, luaScript_select);
        if (_sha1 == null || _sha1.isEmpty()) {
            throw new Exception("can not load script");
        }
        sel_sha = _sha1;
    }

    public Map<Integer, Integer> getMapVal(String value) {
        return getManagerScript().getMapVal(value);
    }

    @Override
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId) {
        return getManagerScript().isNeedResetInItem(player, config, itemId, map);
    }

    @Override
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId) throws Exception {
        getManagerScript().resetInItem(player, config, itemId, map);
    }

    @Override
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData) {
//        return super.updateReward(config, record, activitEntryId, extendData);
        
        return   getManagerScript().updateReward(config, record, activitEntryId, extendData, map);
    }
    
    public static void reloadScripts(){
        String _buy = LogicScriptsUtils.getIActivityReloadLuaScript().reloadBuyLua(luaScript_buy);
        if(!luaScript_buy.equals(_buy)){
            buy_sha = "";
            luaScript_buy = _buy;
        }
        String _select = LogicScriptsUtils.getIActivityReloadLuaScript().reloadSelectLua(luaScript_select);
        if (!luaScript_select.equals(_select)) {
            sel_sha = "";
            luaScript_select = _select;
        }
    }
    
    @Override
	public ActivityRecord checkRecord(ActivityRecord record, ActivityConfigure config, Integer itemId) {
		return getManagerScript().checkRecord(record,config,itemId,map);
	}
}
