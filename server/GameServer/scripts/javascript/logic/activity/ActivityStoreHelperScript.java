package javascript.logic.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CActivityMsg.NewResultSubmitActivity;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityShopItem;
import gm.utils.ActivityUtils;
import logic.activity.ActivityCmdUtils;
import logic.activity.bean.ActivityRecord;
import logic.activity.helper.list.ActivityStoreHelper;
import logic.activity.script.IActivityStoreHelperScript;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.support.MessageUtils;
import redis.base.RedisCmd;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import utils.TimeUtil;

/***
 * 
 * 商品活动helper脚本
 * 
 * @author lihongji
 *
 */

public class ActivityStoreHelperScript implements IActivityStoreHelperScript {


    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITY_SHOP_HELPER_SCRIPT.Value();
    }

    /** 开启活动 **/
    @Override
    public void openActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityShopItem>> map, Logger LOGGER) {
        // 初始化商店条目信息
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        // if(itemMap){不管当前有没有都强行初始化
        itemMap = new HashMap<Integer, ActivityShopItem>();
        map.put(config.getId(), itemMap);
        List<ActivityShopItem> list = queryItemList(config);
        if (list == null || list.isEmpty()) {
            LOGGER.info("活动id=" + config.getId() + "没有配置商品信息");
            return;
        }
        for (ActivityShopItem item : list) {
            itemMap.put(item.getId(), item);
        }
    }

    /** 查询条目 **/
    @Override
    public List<ActivityShopItem> queryItemList(ActivityConfigure config) {
        List<Integer> list = config.getItemsList();
        List<ActivityShopItem> itemList = new ArrayList<>();
        for (Integer itemId : list) {
            ActivityShopItem item = ActivityUtils.queryShopItemById(itemId);
            itemList.add(item);
        }
        return itemList;
    }

    /** 关闭活动 **/
    @Override
    public void closeActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityShopItem>> map) {
        map.remove(config.getId());
    }

    /** 修改活动 **/
    @Override
    public void changeActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityShopItem>> map, Logger LOGGER) {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null) {
            openActivity(config, map, LOGGER);
        } else {
            List<ActivityShopItem> list = queryItemList(config);
            for (ActivityShopItem item : list) {
                ActivityShopItem exist = itemMap.get(item.getId());
                if (exist == null) {
                    itemMap.put(item.getId(), item);
                } else {
                    exist.setBatchBuy(item.getBatchBuy());
                    exist.setGoods(item.getGoods());
                    exist.setLimitDes(item.getLimitDes());
                    exist.setLimitType(item.getLimitType());
                    exist.setLimitVal(item.getLimitVal());
                    exist.setName(item.getName());
                    exist.setOpen(item.getOpen());
                    exist.setRank(item.getRank());
                    exist.setPrice(item.getPrice());
                    exist.setSerLimit(item.getSerLimit());
                    exist.setTag(item.getTag());
                }
            }
        }
    }

    /** 组装数据 **/
    @Override
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder itemBuild,
            Integer itemId, ActivityConfigure config, Player player,
            Map<Integer, Map<Integer, ActivityShopItem>> map) {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return null;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            return null;
        }
        itemBuild.setId(item.getId());
        itemBuild.setType(config.getType());
        itemBuild.setName(item.getName());
        itemBuild.setDetails(item.getLimitDes());
        itemBuild.setTarget(item.getPrice());
        itemBuild.setReward(item.getGoods());
        JSONObject obj = new JSONObject();
        obj.put("batchBuy", item.getBatchBuy());
        obj.put("limitType", item.getLimitType());
        obj.put("limitVal", item.getLimitVal());
        obj.put("serLimit", item.getSerLimit());
        obj.put("tag", item.getTag());
        String extenDate = JSON.toJSONString(obj, true);
        itemBuild.setExtendData(extenDate);
        itemBuild.setRank(item.getRank());
        return itemBuild;
    }

    /** 商店活动 判断是否还能领奖（购买） --就是判断限购次数和个人购买次数 
     * @throws Exception **/
    @Override
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData, Map<Integer, Map<Integer, ActivityShopItem>> map, Logger LOGGER) throws Exception {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            LOGGER.error("activity item map is empty , actId = " + config.getId());
            return true;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            LOGGER.error("activity item is empty , itemId = " + itemId);
            return true;
        }
        if (extendData == null || extendData.isEmpty()) {
            LOGGER.error("activity shop data is empty");
            return true;
        }
        Integer num = Integer.parseInt(extendData);
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_NO_TYPE) {
            return false;
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_DAY_TYPE) {
            if (record == null) {
                return false;
            }
            if (record.getGotCount() + num <= item.getLimitVal()) {
                return false;
            }
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TOTAL_TYPE) {
            if (record == null) {
                return false;
            }
            if (record.getGotCount() + num <= item.getLimitVal()) {
                return false;
            }
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE) {
            Long allNum = 0L;
                allNum = trySelectFromRedis(config, item);
            if (allNum + num <= item.getLimitVal()) {
                return false;
            }
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_ALL_SERVER_TOTAL_TYPE) {
            Long allNum = 0L;
                allNum = trySelectFromRedis(config, item);
            if (allNum + num <= item.getLimitVal()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成商品的主键id
     * 
     * @param config
     * @param item
     * @return
     */
    public String generateKey(ActivityConfigure config, ActivityShopItem item) {
        StringBuffer buffer = new StringBuffer();
        String line = "-";
        buffer.append("Activity").append(line).append("shop").append(line).append(config.getId())
                .append(line).append(item.getId());
        return buffer.toString();
    }

    public String generateTimeKey(ActivityConfigure config, ActivityShopItem item) {
        StringBuffer buffer = new StringBuffer();
        String line = "-";
        buffer.append("Activity").append(line).append("shop").append(line).append(config.getId())
                .append(line).append(item.getId()).append(line).append("Time");
        return buffer.toString();
    }

    // 判断玩家身上的资源是否足够
    @Override
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, Map<Integer, Map<Integer, ActivityShopItem>> map, Logger LOGGER) {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            LOGGER.error("activity item map is empty , actId = " + config.getId());
            return false;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            LOGGER.error("activity item is empty , itemId = " + itemId);
            return false;
        }
        Integer num = Integer.parseInt(extendData);
        Map<Integer, Integer> mapVal = getMapVal(item.getPrice());
        for (Integer key : mapVal.keySet()) {
            Integer singleShopNum = mapVal.get(key);
            mapVal.put(key, singleShopNum * num);
        }
        boolean isEnough = player.getBagManager().enoughByTemplateId(mapVal);
        return isEnough;
    }


    /** 领取活动奖励 成功领取ture 
     * @throws Exception **/
    @Override
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record,
            Map<Integer, Map<Integer, ActivityShopItem>> map, Logger LOGGER) throws Exception {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            LOGGER.error("activity item map is empty , actId = " + config.getId());
            return false;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            LOGGER.error("activity item is empty , itemId = " + itemId);
            return false;
        }
        Integer num = Integer.parseInt(extendData);
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE
                || item.getLimitType() == ActivityStoreHelper.LIMIT_ALL_SERVER_TOTAL_TYPE) {
            // 如果是全服性的商品 就需要先通过一个是否实际购买来判断商品是否购买成功
            Long alreadyBuyNum = 0L;
                alreadyBuyNum = tryBuyFromRedis(config, item, num);
            if (alreadyBuyNum == 0) {// 表示购买失败
                LOGGER.error("redis oper fail , itemId = " + itemId);
                return false;
            }
        }
        // 扣除资源
        Map<Integer, Integer> mapPrice = getMapVal(item.getPrice());
        for (Integer key : mapPrice.keySet()) {
            Integer singleShopPrice = mapPrice.get(key);
            mapPrice.put(key, singleShopPrice * num);
        }
        boolean isRemove = player.getBagManager().removeItemsByTemplateIdWithCheck(mapPrice, true,
                EReason.ACTIVITY_SHOPPING);
        if (!isRemove) {
            return false;
        }
        // 获得奖励
        Map<Integer, Integer> mapGoods = getMapVal(item.getGoods());
        for (Integer key : mapGoods.keySet()) {
            Integer singleShopNum = mapGoods.get(key);
            mapGoods.put(key, singleShopNum * num);
        }
        record = updateReward(config, record, itemId, extendData, map);
        player.getActivityManager().addRcord(record);
        ActivityCmdUtils.getDefault().sendSingleActivityRecordToPlayer(record, player);
        player.getBagManager().addItems(mapGoods, true, EReason.ACTIVITY_SHOPPING);
        NewResultSubmitActivity.Builder build = NewResultSubmitActivity.newBuilder();
        build.setActivityid(config.getId());
        build.setActivitEntryId(itemId);
        RewardsMsg.Builder rewardBuild = RewardsMsg.newBuilder();
        for (Integer key : mapGoods.keySet()) {
            rewardBuild.setId(key);
            rewardBuild.setNum(mapGoods.get(key));
            build.addRewards(rewardBuild);
        }
        MessageUtils.send(player, build);
        return true;
    }


    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getMapVal(String value) {
        Object val = null;
        value = "".equals(value) ? "{}" : value;
        val = JSON.parseObject(value, Map.class);
        Map<Integer, Integer> mapVal = (Map<Integer, Integer>) val;
        return mapVal;
    }

    @Override
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId,
            Map<Integer, Map<Integer, ActivityShopItem>> map) {
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return false;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            return false;
        }
        ActivityRecord record = player.getActivityManager().getRecord(config.getId(), itemId);
        if (record == null) {
            return false;
        }
        if (TimeUtil.isSameDay(record.getRefreshTime(), new Date())) {
            return false;
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_DAY_TYPE
                || item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE) {
            return true;
        }
        return false;
    }

    @Override
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId,
            Map<Integer, Map<Integer, ActivityShopItem>> map) throws Exception {
        ActivityRecord record = player.getActivityManager().getRecord(config.getId(), itemId);
        if (record == null) {
            return;
        }
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return;
        }
        ActivityShopItem item = itemMap.get(itemId);
        if (item == null) {
            return;
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_DAY_TYPE) {
            record.setGotCount(0);
            record.setProgress(0);
            record.setRefreshTime(new Date());
            player.getActivityManager().addRcord(record);
        }
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE) {
            record.setGotCount(0);
            record.setProgress(0);
            Long allNum = 0L;
                allNum = trySelectFromRedis(config, item);
            record.setExtra(String.valueOf(allNum));
            record.setRefreshTime(new Date());
            player.getActivityManager().addRcord(record);
        }
    }


    @Override
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData,
            Map<Integer, Map<Integer, ActivityShopItem>> map) {
        if (record == null) {
            record = new ActivityRecord(config.getId(), activitEntryId);
        }
        Integer num = Integer.parseInt(extendData);
        record.setGotCount(record.getGotCount() + num);
        record.setProgress(record.getGotCount());
        Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        ActivityShopItem item = itemMap.get(activitEntryId);
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE
                || item.getLimitType() == ActivityStoreHelper.LIMIT_ALL_SERVER_TOTAL_TYPE) {
            try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.ACTIVITY.getType()).getJedis()) {
                String redisNum = jedis.get(generateKey(config, item));
                Integer serverNum = 0;
                if (redisNum != null) {
                    serverNum = Integer.parseInt(redisNum);
                }
                record.setExtra(serverNum + "");
            }catch (Exception e) {
                throw e; 
            }
        }
        return record;
    }

    // 通过redis获得全服限购次数
    public Long trySelectFromRedis(ActivityConfigure config, ActivityShopItem item) throws Exception {
        String key = generateKey(config, item);
        String key2 = generateTimeKey(config, item);
        RedisOper redisOper =
                RedisServices.getRedisService(ERedisType.ACTIVITY.getType()).getRedisOper();
        // 首先判断sha1值是否已经存在
        if (ActivityStoreHelper.sel_sha == null || ActivityStoreHelper.sel_sha.isEmpty()) {
                ActivityStoreHelper._scriptLoad_Select(redisOper, key2);
        }

        // sha1的脚本是否存在
        boolean isExist = (Boolean) redisOper.execute(RedisCmd.scriptexists, key,
                ActivityStoreHelper.sel_sha);
        if (!isExist) {
                ActivityStoreHelper._scriptLoad_Select(redisOper, key);
        }

        Object[] params = new String[6];
        params[0] = ActivityStoreHelper.sel_sha;
        // keycount
        params[1] = "2";
        // key1
        params[2] = key;
        // key2
        params[3] = key2;
        // argvs
        params[4] = String.valueOf(System.currentTimeMillis());
        Long nextTime = TimeUtil.getNextZeroClock();
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_ALL_SERVER_TOTAL_TYPE) {
            nextTime = config.getShowEndTime();
        }
        params[5] = String.valueOf(nextTime);
        Long ids = (Long) redisOper.execute(RedisCmd.evalsha, key, params);
        return ids;
    }

    // 通过redis尝试能否购买全服商品
    @Override
    public Long tryBuyFromRedis(ActivityConfigure config, ActivityShopItem item, Integer num) throws Exception {
        String key = generateKey(config, item);
        RedisOper redisOper =
                RedisServices.getRedisService(ERedisType.ACTIVITY.getType()).getRedisOper();
        // 首先判断sha1值是否已经存在
        if (ActivityStoreHelper.buy_sha == null || ActivityStoreHelper.buy_sha.isEmpty()) {
                ActivityStoreHelper._scriptLoad_Buy(redisOper, key);
        }

        // sha1的脚本是否存在
        boolean isExist = (Boolean) redisOper.execute(RedisCmd.scriptexists, key,
                ActivityStoreHelper.buy_sha);
        if (!isExist) {
                ActivityStoreHelper._scriptLoad_Buy(redisOper, key);
        }

        Object[] params = new String[5];
        params[0] = ActivityStoreHelper.buy_sha;
        // keycount
        params[1] = "1";
        // key
        params[2] = key;
        // argvs
        params[3] = String.valueOf(item.getLimitVal());
        params[4] = String.valueOf(num);
        Long ids = (Long) redisOper.execute(RedisCmd.evalsha, key, params);
        return ids;
    }

	@Override
	public ActivityRecord checkRecord(ActivityRecord record,ActivityConfigure config,Integer itemId,Map<Integer, Map<Integer, ActivityShopItem>> map) {
	    Map<Integer, ActivityShopItem> itemMap = map.get(config.getId());
        ActivityShopItem item = itemMap.get(itemId);
        if (item.getLimitType() == ActivityStoreHelper.LIMIT_TO_ALL_SERVER_DAY_TYPE
                || item.getLimitType() == ActivityStoreHelper.LIMIT_ALL_SERVER_TOTAL_TYPE) {
            if(record==null){
                record = new ActivityRecord(config.getId(), itemId);
            }
        	try (Jedis jedis =
                    RedisServices.getRedisService(ERedisType.ACTIVITY.getType()).getJedis()) {
                String redisNum = jedis.get(generateKey(config, item));
                Integer serverNum = 0;
                if (redisNum != null) {
                    serverNum = Integer.parseInt(redisNum);
                }
                record.setExtra(serverNum + "");
            }catch (Exception e) {
                throw e; 
            }
		}
		return record;
	}

}
