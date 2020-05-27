package javascript.logic.dating;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import data.GameDataManager;
import data.bean.ItemRecoverCfgBean;
import data.bean.TriggerEventCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.ElementCollectionConstant;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.ItemConstantId;
import logic.constant.TriggerEventResult;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.script.IDatingManagerScript;
import logic.dating.trigger.DatingTrigger;
import logic.support.MessageUtils;
import utils.TimeUtil;
import utils.ToolMap;

/***
 * 
 * 约会管理器脚本
 * 
 * @author lihongji
 *
 */
public class DatingManagerScript implements IDatingManagerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DATINGMANAGER_SCRIPT.Value();
    }

    /** 清理相应约会 **/
    @Override
    public void deleteDating(CurrentDatingBean dating,
            Map<Long, CurrentDatingBean> currentDatings) {
        currentDatings.remove(dating.getId());
    }

    /** 清理相应约会 **/
    @Override
    public CityDatingBean deleteCityDating(long datingId,
            Map<Long, CurrentDatingBean> currentDatings) {
        CurrentDatingBean cuDB = currentDatings.get(datingId);
        if (cuDB instanceof CityDatingBean) {
            currentDatings.remove(datingId);
            return (CityDatingBean) cuDB;
        }
        return null;
    }

    /** 获取城市约会 **/
    @Override
    public CityDatingBean getCityDating(long cityDatingId,
            Map<Long, CurrentDatingBean> currentDatings) {
        CurrentDatingBean cuDB = currentDatings.get(cityDatingId);
        if (cuDB instanceof CityDatingBean) {
            return (CityDatingBean) cuDB;
        }
        return null;
    }


    /**
     * 获取当前所有城市约会
     */
    @Override
    public List<CityDatingBean> getCurrentCityDatings(int datingType,
            Map<Long, CurrentDatingBean> currentDatings) {
        return currentDatings.values().stream().filter(dating -> (dating instanceof CityDatingBean))
                .filter(record -> record.getDatingType() == datingType)
                .map(dating -> (CityDatingBean) dating).collect(Collectors.toList());
    }

    /**
     * 获取当前所有城市约会
     */
    @Override
    public List<CityDatingBean> getAllCityDatings(Map<Long, CurrentDatingBean> currentDatings) {
        return currentDatings.values().stream().filter(dating -> (dating instanceof CityDatingBean))
                .map(dating -> (CityDatingBean) dating).collect(Collectors.toList());
    }

    /**
     * 放置约会对象
     */
    @Override
    public void putDatingBean(CurrentDatingBean dating,
            Map<Long, CurrentDatingBean> currentDatings) {
        currentDatings.put(dating.getId(), dating);
    }

    /** 看板娘触发约会是否存在 */
    @Override
    public boolean roleTriggerDatingExists(int roleId, int scriptId,
            Map<Integer, Set<Integer>> roleTriggerScripts) {
        Set<Integer> scripts = roleTriggerScripts.get(roleId);
        if (scripts == null)
            return false;
        return scripts.contains(scriptId);
    }

    @Override
    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid,
            Map<Long, CurrentDatingBean> currentDatings, long datingId) {
        return currentDatings.values().stream()
                .filter(record -> record.getDatingType() == datingType)
                .filter(record -> ((int) record.getRoleIds().get(0)) == roleCid)
                .filter(record -> record.getId() == datingId).findFirst().orElse(null);
    }

    @Override
    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid,
            Map<Long, CurrentDatingBean> currentDatings) {
        return currentDatings.values().stream()
                .filter(record -> record.getDatingType() == datingType)
                .filter(record -> ((int) record.getRoleIds().get(0)) == roleCid).findFirst()
                .orElse(null);
    }


    /** 精灵是否暂用 **/
    @Override
    public CurrentDatingBean OccupyRoleId(int roleCid,
            Map<Long, CurrentDatingBean> currentDatings) {
        return currentDatings.values().stream().filter(record -> record instanceof CityDatingBean)
                .filter(record -> ((int) record.getRoleIds().get(0)) == roleCid).findFirst()
                .orElse(null);
    }

    /** 通过类型和精灵ID获取剧本 **/
    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType,
            List<Integer> roleCidList, Map<Long, CurrentDatingBean> currentDatings) {
        return currentDatings.values().stream()
                .filter(record -> record.getDatingType() == datingType)
                .filter(record -> record.getRoleIds().containsAll(roleCidList)).findFirst()
                .orElse(null);
    }

    /** 获取看板娘约会次数 **/
    @Override
    public int getRoleCityDatingCount(int roleCid, Map<Integer, Integer> roleCityDatingCount) {
        return roleCityDatingCount.getOrDefault(roleCid, 0);
    }

    /** 看板娘城市约会次数 **/
    @Override
    public void addRoleCityDatingCount(int roleCid, Map<Integer, Integer> roleCityDatingCount) {
        int count = getRoleCityDatingCount(roleCid, roleCityDatingCount);
        roleCityDatingCount.put(roleCid, count + 1);
    }

    @Override
    public void tick(Player player) {
        if (checkCityDating(player.getDatingManager())) {
            Date now = new Date();
            // 城市约会的处理
            handleCityDating(now, player);
        }
    }

    /** 检测触发城市约会的时间 **/
    @Override
    public boolean checkCityDating(DatingManager manager) {
        long checkDatingTime = manager.getCheckDatingTime();
        if (checkDatingTime == 0 || checkDatingTime < System.currentTimeMillis()) {
            manager.setCheckDatingTime(TimeUtil.getTodayOfhonur() + TimeUtil.ONE_HOUR);
            return true;
        }
        return false;
    }

    /**
     * 自主处理城市约会
     */
    @Override
    public void handleCityDating(Date now, Player player) {
        // 先进行推进再进行触发判定,顺序不可改变
        collateDatingState(now, player);
        triggerCityDating(now, player);
    }

    /**
     * 触发激活城市约会
     */
    @SuppressWarnings("unchecked")
    @Override
    public void triggerCityDating(Date now, Player player) {
        // 获得离散配置
        List<Integer> triggerTypeList =
                (List<Integer>) GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING)
                        .getData().get(DiscreteDataKey.TRIGGER_ORDER);
        // 根据配置进行城市约会的触发
        DatingTrigger trigger;
        for (int datingType : triggerTypeList) {
            trigger = DatingService.getInstance().getDatingTrigger(datingType);
            if (trigger == null)
                continue;
            trigger.triggerDating(player, now, null);
        }
    }

    /** 日常约会次数变化 **/
    @Override
    public void changeDailyCont(int num, DatingManager manager) {
        if (num == 0)
            return;
        int dailyCont = manager.getDailyCont();
        int totalUsed = dailyCont + num;
        if (totalUsed < 0) {
            totalUsed = 0;
        }
        int max = getDailyCountCfg().getMaxRecoverCount();
        if (totalUsed > max) {
            totalUsed = max;
        }
        if (dailyCont != totalUsed) {
            manager.setDailyCont(totalUsed);
        }
    }

    /** 获取日常约会次数配置表 */
    @Override
    public ItemRecoverCfgBean getDailyCountCfg() {
        List<ItemRecoverCfgBean> beans = GameDataManager.getItemRecoverCfgBeans();
        for (ItemRecoverCfgBean bean : beans) {
            if (bean.getItemId() == ItemConstantId.DAILY_DATING_COUNT) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 整理核对约会状态
     */
    @Override
    public void collateDatingState(Date now, Player player) {
        // 城市约会
        List<CityDatingBean> cityList =
                getAllCityDatings(player.getDatingManager().getCurrentDatings());
        cityList.forEach((cityDating) -> {
            DatingService.getInstance().getDatingTrigger(cityDating).refreshCityDating(player, now,
                    null, null);
        });

    }


    /** 跨天 **/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify,
            Map<Integer, Integer> roleCityDatingCount, Player player) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {// 游戏内跨天（6点）
            roleCityDatingCount.clear();
            resetDailyDating(player);
        }
    }

    /** 时装和道具触发器 **/
    @SuppressWarnings("unchecked")
    @Override
    public void trigger(Object object, Player player) {
        if (object == null)
            return;
        Map<String, Object> in = (Map<String, Object>) object;
        Integer conditionType = (Integer) in.get(EventConditionKey.CONDITION_TYPE);
        if (conditionType == EventConditionType.DONATE_GIFT) {
            checkDonateGift(in, player);
        } else if (conditionType == EventConditionType.DRESS) {
            checkDress(in, player);
        }
    }

    /** 检测喂食是否触发约会 **/
    @Override
    public void checkDonateGift(Map<String, Object> in, Player player) {
        List<TriggerEventCfgBean> triggerEventList =
                GameDataManager.getTriggerEventCfgContainer().getList();
        List<TriggerEventCfgBean> triggerConditionList = triggerEventList.stream()
                .filter(e -> (int) e.getResult()
                        .get(EventConditionKey.EVENT_RESULT_TYPE) == TriggerEventResult.DATING)
                .collect(Collectors.toList());
        if (triggerConditionList == null)
            return;
        if (in.get(EventConditionKey.GIFT_CID) == null)
            return;
        // 获取真实Id
        int real_giftId = (int) in.get(EventConditionKey.GIFT_CID);
        int real_roleId = (int) in.get(EventConditionKey.ROLE_CID);
        for (TriggerEventCfgBean triggerBean : triggerConditionList) {
            if (triggerBean.getParams() == null
                    || triggerBean.getParams().get(EventConditionKey.GIFT_CID) == null
                    || triggerBean.getParams().get(EventConditionKey.ROLE_CID) == null)
                continue;
            int sys_giftId = (int) triggerBean.getParams().get(EventConditionKey.GIFT_CID);
            int sys_roleId = (int) triggerBean.getParams().get(EventConditionKey.ROLE_CID);
            if (real_roleId == sys_roleId && real_giftId == sys_giftId) {
                Map data = ToolMap.getMap(EventConditionKey.EVENT_RESULT_DATA,
                        triggerBean.getResult());
                int scriptCid = ToolMap.getInt(EventConditionKey.SCRIPT_ID, data);
                int roleId = ToolMap.getInt(EventConditionKey.ROLE_CID, data);
                Map<Integer, Set<Integer>> roleTriggerScripts =
                        player.getDatingManager().getRoleTriggerScripts();
                if (roleTriggerScripts.get(sys_roleId) != null
                        && roleTriggerScripts.get(sys_roleId).contains(scriptCid))
                    return;
                org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.Builder builder =
                        org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.newBuilder();

                Set<Integer> record = roleTriggerScripts.get(roleId);
                if (record == null) {
                    record = new HashSet<Integer>();
                    roleTriggerScripts.put(roleId, record);
                }
                record.add(scriptCid);
                List<Integer> list = new ArrayList<Integer>();
                list.add(scriptCid);
                builder.setRoleId(roleId).addAllDatingRuleCid(list);
                MessageUtils.send(player, builder);
                // 图鉴收集
                player.getElementCollectionManager().recordElement(player,
                        ElementCollectionConstant.KEY_EVENT, triggerBean.getId(), false);
            }
        }

    }

    /** 检测时装 **/
    @Override
    public void checkDress(Map<String, Object> in, Player player) {
        List<TriggerEventCfgBean> triggerEventList =
                GameDataManager.getTriggerEventCfgContainer().getList();
        List<TriggerEventCfgBean> triggerConditionList = triggerEventList.stream()
                .filter(e -> (int) e.getResult()
                        .get(EventConditionKey.EVENT_RESULT_TYPE) == TriggerEventResult.DATING)
                .collect(Collectors.toList());
        if (triggerConditionList == null)
            return;
        if (in.get(EventConditionKey.DRESS_ID) == null)
            return;
        // 获取真实Id
        int real_dressId = (int) in.get(EventConditionKey.DRESS_ID);
        int real_roleId = (int) in.get(EventConditionKey.ROLE_CID);
        for (TriggerEventCfgBean triggerBean : triggerConditionList) {
            if (triggerBean.getParams() == null
                    || triggerBean.getParams().get(EventConditionKey.DRESS_ID) == null
                    || triggerBean.getParams().get(EventConditionKey.ROLE_CID) == null)
                continue;
            int sys_dressId = (int) triggerBean.getParams().get(EventConditionKey.DRESS_ID);
            int sys_roleId = (int) triggerBean.getParams().get(EventConditionKey.ROLE_CID);
            if (real_roleId == sys_roleId && real_dressId == sys_dressId) {

                Map data = ToolMap.getMap(EventConditionKey.EVENT_RESULT_DATA,
                        triggerBean.getResult());
                Map<Integer, Set<Integer>> roleTriggerScripts =
                        player.getDatingManager().getRoleTriggerScripts();
                int scriptCid = ToolMap.getInt(EventConditionKey.SCRIPT_ID, data);
                int roleId = ToolMap.getInt(EventConditionKey.ROLE_CID, data);
                if (roleTriggerScripts.get(sys_roleId) != null
                        && roleTriggerScripts.get(sys_roleId).contains(scriptCid))
                    return;
                org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.Builder builder =
                        org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.newBuilder();

                Set<Integer> record = roleTriggerScripts.get(roleId);
                if (record == null) {
                    record = new HashSet<Integer>();
                    roleTriggerScripts.put(roleId, record);
                }
                record.add(scriptCid);
                List<Integer> list = new ArrayList<Integer>();
                list.add(scriptCid);
                builder.setRoleId(roleId).addAllDatingRuleCid(list);
                MessageUtils.send(player, builder);
                // 图鉴收集
                player.getElementCollectionManager().recordElement(player,
                        ElementCollectionConstant.KEY_EVENT, triggerBean.getId(), false);
            }
        }

    }

    /** 重置当前日常约会次数 **/
    @Override
    public void resetDailyDating(Player player) {
        ItemRecoverCfgBean bean = getItemRecoverCfg(ItemConstantId.DAILY_DATING_COUNT);
        int dailyCont = player.getDatingManager().getDailyCont();
        if (dailyCont < bean.getMaxRecoverCount()) {
            player.getBagManager().addItem(ItemConstantId.DAILY_DATING_COUNT,
                    bean.getMaxRecoverCount() - dailyCont, true, EReason.DAILY_DATING);
        }
    }

    /**
     * 获取精力恢复的配置
     * 
     * @return
     */
    @Override
    public ItemRecoverCfgBean getItemRecoverCfg(int id) {
        List<ItemRecoverCfgBean> list = GameDataManager.getItemRecoverCfgBeans();
        for (ItemRecoverCfgBean bean : list) {
            if (bean.getItemId() == id) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public void createRoleInitialize(Player player) {
        // 重置当前日常约会次数
        resetDailyDating(player);
    }


    /** 接受邀请 **/
    @Override
    public void acceptDating(boolean accept, Player player) {
        DatingService.getInstance().getDatingTrigger(DatingTypeConstant.DATING_TYPE_RESERVE)
                .acceptDating(accept, player);
    }

    /** 检测预定约会是否超时 **/
    @Override
    public boolean checkReserveDating(Player player) {
        return DatingService.getInstance().getDatingTrigger(DatingTypeConstant.DATING_TYPE_RESERVE)
                .checkReserveDating(player);
    }

    /** 移除手机约会 **/
    @Override
    public void removePhoneDating(Player player, CurrentDatingBean record) {
        if (player.getDatingManager().getCurrentDatings() == null
                || player.getDatingManager().getCurrentDatings().size() <= 0)
            return;
        Iterator<Entry<Long, CurrentDatingBean>> iterator =
                player.getDatingManager().getCurrentDatings().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Long, CurrentDatingBean> entry = iterator.next();
            if (entry != null
                    && entry.getValue().getDatingType() == DatingTypeConstant.DATING_TYPE_PHONE) {
                iterator.remove();
            }
        }
    }

}
