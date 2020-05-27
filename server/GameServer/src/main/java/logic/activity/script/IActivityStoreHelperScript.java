package logic.activity.script;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;

import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityShopItem;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import script.IScript;

/***
 * 
 * 商品活动helper
 * 
 * @author lihongji
 *
 */
public interface IActivityStoreHelperScript extends IScript {


    /** 开启活动 **/
    public void openActivity(ActivityConfigure config,Map<Integer, Map<Integer, ActivityShopItem>> map,Logger LOGGER);

    /** 查询条目 **/
    public List<ActivityShopItem> queryItemList(ActivityConfigure config);

    /** 关闭活动 **/
    public void closeActivity(ActivityConfigure config,Map<Integer, Map<Integer, ActivityShopItem>> map);

    /** 修改活动 **/
    public void changeActivity(ActivityConfigure config,Map<Integer, Map<Integer, ActivityShopItem>> map,Logger LOGGER);

    /** 组装数据 **/
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder itemBuild,
            Integer itemId, ActivityConfigure config, Player player,Map<Integer, Map<Integer, ActivityShopItem>> map);

    /** 商店活动 判断是否还能领奖（购买） --就是判断限购次数和个人购买次数 **/
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData,Map<Integer, Map<Integer, ActivityShopItem>> map,Logger LOGGER) throws Exception;

    /**
     * 生成商品的主键id
     * 
     * @param config
     * @param item
     * @return
     */
    public String generateKey(ActivityConfigure config, ActivityShopItem item);

    public String generateTimeKey(ActivityConfigure config, ActivityShopItem item);

    // 判断玩家身上的资源是否足够
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData,Map<Integer, Map<Integer, ActivityShopItem>> map,Logger LOGGER);

    /** 领取活动奖励 成功领取ture **/
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record,Map<Integer, Map<Integer, ActivityShopItem>> map,Logger LOGGER) throws Exception;


    public Map<Integer, Integer> getMapVal(String value);

    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId,Map<Integer, Map<Integer, ActivityShopItem>> map);

    public void resetInItem(Player player, ActivityConfigure config, Integer itemId,Map<Integer, Map<Integer, ActivityShopItem>> map) throws Exception;

    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData,Map<Integer, Map<Integer, ActivityShopItem>> map);

    // 通过redis获得全服限购次数
    public Long trySelectFromRedis(ActivityConfigure config, ActivityShopItem item) throws Exception;

    // 通过redis尝试能否购买全服商品
    public Long tryBuyFromRedis(ActivityConfigure config, ActivityShopItem item, Integer num) throws Exception;

	public ActivityRecord checkRecord(ActivityRecord record, ActivityConfigure config,Integer itemId , Map<Integer, Map<Integer, ActivityShopItem>> map);
}
