package logic.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;
import org.game.protobuf.s2c.S2CRoleMsg.RoleStatusInfo;
import org.game.protobuf.s2c.S2CRoleMsg.SwitchRoleResult;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.CityStateCfgBean;
import data.bean.DiscreteDataCfgBean;
import data.bean.HeroCfgBean;
import data.bean.ItemCfgBean;
import data.bean.ItemRecoverCfgBean;
import data.bean.RoleCfgBean;
import data.bean.RoomCfgBean;
import logic.bag.BagManager;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.ILoginInit;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.city.build.BuildingConstant;
import logic.constant.ConstDefine;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EAcrossDayType;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EItemType;
import logic.constant.EReason;
import logic.constant.ElementCollectionConstant;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.constant.RoleConstant;
import logic.functionSwitch.FunctionSwitchService;
import logic.item.ItemPackageHelper;
import logic.item.ItemUtils;
import logic.item.bean.DressItem;
import logic.item.bean.Item;
import logic.item.bean.RoomItem;
import logic.log.bean.ItemLog;
import logic.msgBuilder.ItemMsgBuilder;
import logic.msgBuilder.RoleMsgBuilder;
import logic.role.bean.Role;
import logic.role.bean.RoleTouch;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.CommonUtil;
import utils.ExceptionEx;
import utils.TimeUtil;
import utils.ToolMap;

/**
 * 
 * @Description 看板娘管理器
 * @author LiuJiang
 * @date 2018年6月13日 上午11:42:24
 *
 */
public class RoleManager extends PlayerBaseFunctionManager implements IRoleJsonConverter,
        ILoginInit, IAcrossDay, ICreatePlayerInitialize, ICreateRoleInitialize {
    private static final Logger LOGGER = Logger.getLogger(RoleManager.class);
    /** 看板娘 */
    Map<Integer, Role> roles = new HashMap<Integer, Role>();
    /** 待激活看板娘 */
    Map<Integer, Role> unActivate = new HashMap<>();

    /** 当前使用的看板娘模板id */
    int nowRole;
    /**
     * 上次重置城市约会次数时间(跨天重置)
     * 
     * lastResetCityDatingCountTime
     */
    long lastTime;

    RoleTouch roleTouch;

    /** 精灵刷新时间 **/
    long roleStateTime;

    /** 预指定精灵id **/
    private transient int preRoleId;

    @Override
    public void createRoleInitialize() throws Exception {
        ItemRecoverCfgBean cfgBean = getCfg(ItemConstantId.DAILY_TOUCH_ROLE_TIMES);
        int recoverCount = cfgBean.getRecoverCount();
        if (roleTouch == null) {
            roleTouch = new RoleTouch();
        }
        roleTouch.setTouchTimes(recoverCount);
        roleTouch.setLastRecoverTime(new Date());
    }

    @Override
    public void createPlayerInitialize() {
        // 检测精灵是否开启
        checkActivateRole();
    }

    @Override
    public void loginInit() {
        checkResetCityTriggerCount();
    }

    @Override
    public void tick() {
        try {
            updateMood();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }

        // 刷新精灵状态
        refreshRoleState();
    }

    public void checkActivateRole() {
        Iterator<Entry<Integer, Role>> iterator = unActivate.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Role> entry = iterator.next();
            if (entry != null) {
                RoleCfgBean cfg = GameDataManager.getRoleCfgBean(entry.getValue().getCid());
                if (cfg != null && cfg.getIsOpen()) {
                    // 重新激活精灵
                    activateRole(entry.getValue().getCid());
                    // 移除未激活的精灵
                    iterator.remove();
                }
            }
        }
        // for (Role role : unActivate.values()) {
        // RoleCfgBean cfg = GameDataManager.getRoleCfgBean(role.getCid());
        // if (cfg != null && cfg.getIsOpen()) {
        // // 重新激活精灵
        // activateRole(role.getCid());
        // // 移除未激活的精灵
        // unActivate.remove(role.getCid());
        // }
        // }
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            checkResetCityTriggerCount();
            // 重置看板娘触摸次数
            checkResetTouchCount();
        }
    }

    public void checkResetTouchCount() {
        ItemRecoverCfgBean cfgBean = getCfg(ItemConstantId.DAILY_TOUCH_ROLE_TIMES);
        if (cfgBean == null) {
            return;
        }
        int recoverCount = cfgBean.getRecoverCount();
        if (roleTouch == null) {
            roleTouch = new RoleTouch();
        }
        roleTouch.setTouchTimes(recoverCount);
        roleTouch.setLastRecoverTime(new Date());
        ItemList.Builder itemChange = ItemList.newBuilder();
        List<Item> items = ItemUtils.createItems(ItemConstantId.PLAYER_ACTIVE, recoverCount);
        ItemMsgBuilder.addItemMsg(itemChange, ChangeType.UPDATE, items.get(0));
        // 发送
        MessageUtils.send(player, itemChange);
    }

    public ItemRecoverCfgBean getCfg(int templateId) {
        List<ItemRecoverCfgBean> cfgList = GameDataManager.getItemRecoverCfgBeans();
        for (ItemRecoverCfgBean cfg : cfgList) {
            if (cfg.getItemId() == templateId) {
                return cfg;
            }
        }
        return null;
    }

    /** 当前看板娘cid */
    public int getNowRoleCid() {
        return nowRole;
    }

    /** 当前看板娘cid */
    public void setNowRole(int nowRole) {
        this.nowRole = nowRole;
    }

    /** 当前看板娘 */
    public Role getNowRole() {
        return roles.get(nowRole);
    }

    /** 获取喂食(送礼)的精灵 **/
    public Role getFeedRole() {
        if (preRoleId == 0)
            return getNowRole();
        return roles.get(preRoleId);
    }

    /** 获取看板娘精灵 */
    public Role getRole(int cid) {
        Role role = roles.get(cid);
        if (role == null) {
            role = unActivate.get(cid);
        }
        return role;
    }

    /**
     * 通过heroId查询看板娘id
     */
    public int getRoleCidByHeroCid(int heroId) {
        HeroCfgBean herocfg = GameDataManager.getHeroCfgBean(heroId);
        return herocfg == null ? 0 : herocfg.getRole();
    }

    public Map<Integer, Role> getRoles() {
        return roles;
    }

    public long getLastResetCityDatingCountTime() {
        return lastTime;
    }

    public void setLastResetCityDatingCountTime(long lastResetCityDatingCountTime) {
        this.lastTime = lastResetCityDatingCountTime;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RoleTouch getRoleTouch() {
        if (roleTouch == null) {
            DiscreteDataCfgBean discreteDataCfgBean =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.INIT_PLAYER);
            Map dataMap = discreteDataCfgBean.getData();
            Map<Integer, Integer> goods = (Map<Integer, Integer>) dataMap.get("goods");
            int value = goods.get(ItemConstantId.DAILY_TOUCH_ROLE_TIMES);
            roleTouch = new RoleTouch(value, new Date());
        }
        return roleTouch;
    }

    public void setRoleTouch(RoleTouch roleTouch) {
        this.roleTouch = roleTouch;
    }

    /**
     * 激活精灵
     * 
     * @param player
     * @param cid
     */
    @SuppressWarnings("unchecked")
    public void activateRole(int cid) {
        // 判断是否已激活该精灵
        if (roles.get(cid) != null) {
            return;
        }
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(cid);
        // 暂未开放(不开放的也将看板娘给他,只是不通知前端有这个看板娘)
        if (cfg == null || !cfg.getIsOpen()) {
            Role role = new Role(cid, 0);
            unActivate.put(cid, role);
            return;
        }
        boolean code = unActivate.containsKey(cid);
        int extraFavor = (code == false ? 0 : unActivate.get(cid).getFavor());

        Player player = getPlayer();
        // 可能创建角色就有2个精灵
        // 解锁礼品
        List<Integer> unlockGiftList = Lists.newArrayList();
        // 解锁爱好
        List<Integer> unlockHobbyList = Lists.newArrayList();

        // 默认解锁礼品
        Map<Integer, Integer> giftMap = cfg.getGift();
        for (Entry<Integer, Integer> e : giftMap.entrySet()) {
            if (e.getValue() != 0) {
                unlockGiftList.add(e.getKey());
            }
        }
        // 默认解锁爱好
        Map<Integer, Integer> hobbyMap = cfg.getHobby();
        for (Entry<Integer, Integer> e : hobbyMap.entrySet()) {
            if (e.getValue() == 0) {
                unlockHobbyList.add(e.getKey());
            }
        }
        Role role = new Role(cid, cfg.getFavor() + extraFavor, 60, unlockGiftList, unlockHobbyList,
                Lists.newArrayList(), Maps.newHashMap(), 0, true, Lists.newArrayList(), 0,
                System.currentTimeMillis(), Lists.newArrayList());
        int defaultDressId = cfg.getRoleModel();
        BaseGoods defaultDress = GameDataManager.getBaseGoods(defaultDressId);
        if (defaultDress != null) {
            // 初始默认时装 & 穿戴在该精灵上
            List<Item> items =
                    player.getBagManager().addItem(defaultDressId, 1, true, EReason.ROLE_ACTIVE);
            DressItem dressItem =
                    (DressItem) player.getBagManager().getItemOrigin(items.get(0).getId());
            dressItem.setRoleId(role.getCid());// 时装绑定精灵
            role.setDressId(dressItem.getId());// 绑定唯一id
        }
        roles.put(role.getCid(), role);
        if (nowRole == 0) {
            setNowRole(role.getCid());
        }
        RoleInfo.Builder builder = RoleMsgBuilder.createRoleInfo(ChangeType.ADD, player, role);
        MessageUtils.send(player, builder);
        //激活精灵设置喂食未当前时间和约会未当前时间
        role.setFeedFoodTime(System.currentTimeMillis());
        role.setDailyTime(System.currentTimeMillis());
        // 图鉴系统
        player.getElementCollectionManager().recordElement(player,
                ElementCollectionConstant.KEY_ROLE, role.getCid(), false);
        Map<String, Object> in = Maps.newConcurrentMap();
        in.put(EventConditionKey.EVENT_RESULT_DATA, cid);

        Map<String, Object> info = Maps.newHashMap();
        info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.ADD_ROLE);
        info.put(BuildingConstant.EVENT_RESULT_DATA, role.getCid());
        player._fireEvent(info, EEventType.UNLOCK_BUILDING.value());
        player._fireEvent(info, EEventType.MAINDATING_ACTIVE.value());
    }

    /**
     * 切换看板娘
     */
    public void switchRole(String roleStr) {
        int roleId = Integer.parseInt(roleStr);
        Role role = roles.get(roleId);
        if (role == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_ROLE, "精灵不存在:" + roleId);
            return;
        }
        if (role.getCid() == nowRole) {
            MessageUtils.throwCondtionError(GameErrorCode.ROLE_STATUS_ERROR, "重复看板娘" + roleId);
            return;
        }
        List<Role> roleList = new ArrayList<>();
        Role oldRole = null;
        if (nowRole != 0) {
            oldRole = getNowRole();
            roleList.add(oldRole);
        }
        nowRole = role.getCid();
        SwitchRoleResult.Builder builder = SwitchRoleResult.newBuilder();
        builder.addRolestatus(
                RoleStatusInfo.newBuilder().setRoleId(Long.toString(role.getCid())).setStatus(1));
        if (oldRole != null) {
            builder.addRolestatus(RoleStatusInfo.newBuilder()
                    .setRoleId(Long.toString(oldRole.getCid())).setStatus(0));
        }
        MessageUtils.send(player, builder);
    }

    /**
     * 修改心情值
     * 
     * @param role
     */
    public void changeMood(Role role, int changeNum, EReason reason) {
        if (changeNum == 0) {
            return;
        }
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ROLE_MOOD_FAVOR);
        Map<String, Integer> config = null;
        int changeFavor = 0;
        if (changeNum > 0) {
            config = ToolMap.getMap(DiscreteDataKey.CEILING, cfg.getData());
            int mood = ToolMap.getInt(DiscreteDataKey.MOOD, config);
            int ratio = ToolMap.getInt(DiscreteDataKey.RATIO, config);
            if (role.getMood() + changeNum > mood) {
                // 溢出的心情值
                changeFavor = (int) ((role.getMood() + changeNum - mood) * (ratio / 10000.0d));
                role.setMood(mood);
            } else {
                role.setMood(role.getMood() + changeNum);
            }
        } else {
            config = ToolMap.getMap(DiscreteDataKey.FLOOR, cfg.getData());
            int mood = ToolMap.getInt(DiscreteDataKey.MOOD, config);
            int ratio = ToolMap.getInt(DiscreteDataKey.RATIO, config);
            if (role.getMood() < mood) {
                changeFavor = (int) ((changeNum) * (ratio / 10000.0d));
                role.setMood(Math.max(0, role.getMood() + changeNum));
            } else if (role.getMood() + changeNum < mood) {
                changeFavor = (int) ((mood - role.getMood() + changeNum) * (ratio / 10000.0d));
                role.setMood(Math.max(0, role.getMood() + changeNum));
            } else {
                role.setMood(role.getMood() + changeNum);
            }
        }
        // 修改好感度
        if (changeFavor != 0) {
            changeFavor(role, changeFavor, reason);
        }

        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(ChangeType.UPDATE);
        builder.setMood(role.getMood());
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        MessageUtils.send(getPlayer(), builder);
    }

    /**
     * 修改好感度
     * 
     * @param role
     */
    public void changeFavor(Role role, int changeNum, EReason reason) {
        // 如果好感度已达临界点 & 是加好感度操作(取消临界值设定)
        if (role == null || changeNum == 0) {
            return;
        }
        int nowFavor = role.getFavor();
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(role.getCid());
        int[] favorLevels = cfg.getFavorLevels();
        int nowLvl = 0, maxLvl = favorLevels.length - 1;

        for (int i = favorLevels.length - 1; i >= 0; i--) {
            if (nowFavor >= favorLevels[i]) {
                nowLvl = i;
                break;
            }
        }
        if (changeNum < 0) {
            // 扣除好感度，不能掉级
            role.setFavor(Math.max(0, Math.max(nowFavor + changeNum, favorLevels[nowLvl])));
            role.setCriticalPoint(false);
        } else {
            // 已达最大好感度
            if (nowFavor >= favorLevels[maxLvl]) {
                return;
            }

            int nextLvl = Math.min(maxLvl, nowLvl + 1);
            if (nowFavor + changeNum >= favorLevels[nextLvl]) {
                role.setFavor(favorLevels[nextLvl]);
                role.setCriticalPoint(true);
            } else {
                role.setFavor(nowFavor + changeNum);
            }
        }
        try {
            ItemLog bean = LogBeanFactory.createItemConsumLog(player, ItemConstantId.ROLE_FAVOR,
                    ItemConstantId.ROLE_FAVOR, changeNum, role.getFavor(), reason.value(), null);
            LogProcessor.getInstance().sendLog(bean);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }

        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(ChangeType.UPDATE);
        builder.setFavor(role.getFavor());
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        builder.setFavorCriticalPoint(role.isCriticalPoint());
        MessageUtils.send(getPlayer(), builder);
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.ROLE_FAVOR);
        in.put(EventConditionKey.OLD_FAVOR, nowFavor);
        in.put(EventConditionKey.NOW_FAVOR, role.getFavor());
        in.put(EventConditionKey.ROLE_CID, cfg.getId());
        player._fireEvent(in, EEventType.ROLE_CHANGE.value());
        // 触发主线章节激活通知
        player._fireEvent(null, EEventType.MAINDATING_ACTIVE.value());
    }

    /**
     * 解锁礼品
     * 
     * @param roleCid
     * @param giftCid
     */
    public void unlockGift(int roleCid, int giftCid) {
        Role role = getPlayer().getRoleManager().getRole(roleCid);
        if (role == null) {
            return;
        }
        // if (role.getUnlockGift().contains(giftCid)) {
        // return;
        // }
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(roleCid);
        if (!cfg.getGift().containsKey(giftCid)) {
            MessageUtils.throwCondtionError(GameErrorCode.THIS_ROLE_NOT_FOND_GIFT,
                    "role cfg not contain gift, giftCid=" + giftCid);
        }
        role.getUnlockGift().add(giftCid);
        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(ChangeType.UPDATE);
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        builder.addAllUnlockGift(role.getUnlockGift());
        MessageUtils.send(getPlayer(), builder);
    }

    /**
     * 解锁爱好
     * 
     * @param roleCid
     */
    public void unlockHobby(int roleCid, int hobbyCid) {
        Role role = player.getRoleManager().getRole(roleCid);
        if (role == null) {
            return;
        }
        role.getUnlockHobby().add(hobbyCid);
        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(ChangeType.UPDATE);
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        builder.addAllUnlockHobby(role.getUnlockHobby());
        MessageUtils.send(getPlayer(), builder);
    }

    public void changeRoom(Player player, int roleId, int roomId) {
        Role role = getRole(roleId);
        if (role == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_ROLE, "精灵不存在");
            return;
        }
        BagManager bagManager = getPlayer().getBagManager();
        List<Item> itemList = bagManager.getItemCopyByTemplateId(roomId);

        if (itemList.isEmpty() || !(itemList.get(0) instanceof RoomItem)) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "房间不存在");
            return;
        }
        RoomItem roomItem = (RoomItem) bagManager.getItemOrigin(itemList.get(0).getId());
        RoomCfgBean roomCfgBean = GameDataManager.getRoomCfgBean(roomItem.getTemplateId());
        int belongToRole = roomCfgBean.getBelongToRole();
        if (belongToRole != 0 && belongToRole != role.getCid()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "更换房间：看板娘与房间不匹配");
            return;
        }
        List<Role> roleList = changeRoom(role, roomItem);
        RoleMsgBuilder.sendRoleMsg(player, org.game.protobuf.s2c.S2CShareMsg.ChangeType.UPDATE,
                roleList.toArray(new Role[] {}));

        org.game.protobuf.s2c.S2CRoleMsg.ChangeRoom.Builder builder =
                org.game.protobuf.s2c.S2CRoleMsg.ChangeRoom.newBuilder();
        MessageUtils.send(player, builder);
    }

    /**
     * 更换房间
     */
    public List<Role> changeRoom(Role role, RoomItem newRoom) {
        List<Role> roleList = Lists.newArrayList();
        RoomItem oldRoom = (RoomItem) getPlayer().getBagManager().getItemOrigin(role.getRoomId());
        if (oldRoom != null) {
            oldRoom.setRoleId(0);
        }
        int oldRoleCid = newRoom.getRoleId();
        if (oldRoleCid != 0L) {
            Role oldRole = getPlayer().getRoleManager().getRole(oldRoleCid);
            if (oldRole != null) {
                oldRole.setRoomId(0);
            }
            roleList.add(oldRole);
            calcEffect(oldRole);
        }
        newRoom.setRoleId(role.getCid());
        role.setRoomId(newRoom.getId());
        calcEffect(role);
        roleList.add(role);
        return roleList;
    }

    /**
     * 解锁房间
     */
    @SuppressWarnings("unchecked")
    public void unlockRoom(Player player, int roomCid) {
        RoomCfgBean roomCfgBean = GameDataManager.getRoomCfgBean(roomCid);
        if (roomCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "房间配置不存在");
            return;
        }
        boolean isExist = player.getBagManager().enoughByTemplateId(roomCid, 1);
        if (isExist) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "房间已经解锁");
            return;
        }
        BagManager bagM = getPlayer().getBagManager();
        boolean code = bagM.removeItemsByTemplateIdWithCheck(roomCfgBean.getUnlockCost(), true,
                EReason.ROOM_UNLOCK);
        if (!code) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "道具不足");
            return;
        }
        bagM.addItem(roomCfgBean.getId(), 1, true, EReason.ROOM_UNLOCK);

        // 回发消息
        org.game.protobuf.s2c.S2CRoleMsg.UnlockRoom.Builder builder =
                org.game.protobuf.s2c.S2CRoleMsg.UnlockRoom.newBuilder();
        logic.support.MessageUtils.send(player, builder);
    }

    /**
     * 赠送礼物
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void donate(String roleStrId, int itemCid, int number) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ROLE_DONATE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:RoleDonate");
        }
        int roleId = Integer.parseInt(roleStrId);
        Role role = roles.get(roleId);
        if (role == null) {
            MessageUtils.throwConfigError(GameErrorCode.NOT_FOND_ROLE, "精灵未找到:" + roleId);
            return;
        }
        ItemCfgBean itemCfgBean = GameDataManager.getItemCfgBean(itemCid);
        if (itemCfgBean.getSuperType() != EItemType.GIFT.getValue()) {
            MessageUtils.throwConfigError(GameErrorCode.CLIENT_PARAM_IS_ERR, "道具不符合:" + itemCid);
            return;
        }
        RoleCfgBean roleCfgBean = GameDataManager.getRoleCfgBean(roleId);
        if (itemCfgBean == null || roleCfgBean == null) {
            MessageUtils.throwConfigError(GameErrorCode.CLIENT_PARAM_IS_ERR, "配置未找到:" + itemCid);
            return;
        }
        Map likeFood = roleCfgBean.getLikeFood();
        Map likeGift = roleCfgBean.getLikeGift();
        // 增益值,表里配的是百分比
        int effectValue = 0;
        boolean favorite = false;
        if (likeFood.containsKey(itemCid)) {
            effectValue = (int) likeFood.get(itemCid);
            favorite = true;
        } else if (likeGift.containsKey(itemCid)) {
            effectValue = (int) likeGift.get(itemCid);
            favorite = true;
        }

        int roleState = 0;
        if (itemCfgBean.getSubType() == 1) {
            roleState = RoleConstant.HUNGER;
        } else if (itemCfgBean.getSubType() == 2) {
            roleState = RoleConstant.ANGRY;
        }
        Map<Integer, Integer> useProfit = new HashMap<>();
        ItemPackageHelper.unpack(itemCfgBean.getUseProfit(), null, useProfit);

        ItemUtils.autoMapByRadio(useProfit, effectValue / 100.0);
        Map<Integer, Integer> good = Maps.newHashMap();
        good.put(itemCid, number);
        // 扣除
        boolean isEnough =
                player.getBagManager().removeItemsByTemplateIdWithCheck(good, true, EReason.TOUCH);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "道具不足");
            return;
        }
        addPercentByRoleState(useProfit, role, roleState,RoleConstant.FEED_STATE);
        preRoleId = role.getCid();
        // 给予资源
        player.getBagManager().addItems(useProfit, true, EReason.TOUCH);

        preRoleId = 0;
        // 赠送
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.DONATE_GIFT);
        in.put(EventConditionKey.ROLE_CID, role.getCid());
        in.put(EventConditionKey.GIFT_CID, itemCid);
        player._fireEvent(in, EEventType.ROLE_CHANGE.value());

        player._fireEvent(in, EEventType.TRIGGER_CHECK.value());

        S2CRoleMsg.Donate.Builder builder = S2CRoleMsg.Donate.newBuilder();
        if (favorite) {
            builder.setFavoriteId(itemCid);
            role.getFavoriteIds().add(itemCid);
        }
        MessageUtils.send(player, builder);
    }

    /**
     * 换装
     */
    public void dress(String roleId, String dressId) {
        int _roleId = Integer.parseInt(roleId);
        long _dressId = Long.valueOf(dressId);
        Role role = roles.get(_roleId);
        if (role == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_ROLE, "精灵不存在");
            return;
        }
        Item item = player.getBagManager().getItemOrigin(_dressId);
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_DRESS, "时装不存在");
            return;
        }
        DressItem dressItem = (DressItem) item;
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(role.getCid());
        if (cfg == null || cfg.getDress().length <= 0) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_EQUIP_DRESS, "精灵和时装不匹配");
            return;
        }
        boolean code = canDress(cfg.getDress(), dressItem.getTemplateId());
        if (!code) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_EQUIP_DRESS, "精灵和时装不匹配");
            return;
        }
        // 是否已经被其他精灵穿上/如果有其他精灵恢复默认皮肤
        int oldRoleCid = dressItem.getRoleId();
        DressItem defaultItem = null;
        Role oldRole = null;
        if (oldRoleCid != 0) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_EQUIP_DRESS, "精灵和时装不匹配");
            return;
            // oldRole = roles.get(oldRoleCid);
            // RoleCfgBean oldCfg = GameDataManager.getRoleCfgBean(oldRole.getCid());
            // int defaultDressId = oldCfg.getRoleModel();
            // defaultItem =
            // (DressItem) player.getBagManager().getItemCopyByTemplateId(defaultDressId);
            // defaultItem.setRoleId(oldRoleCid);
            // oldRole.setDressId(defaultItem.getId());
        }

        long oldDressId = role.getDressId();
        DressItem oldDressItem = null;
        if (oldDressId != 0) {
            Item oldItem = player.getBagManager().getItemOrigin(oldDressId);
            oldDressItem = (DressItem) oldItem;
            oldDressItem.setRoleId(0);
        }
        role.setDressId(_dressId);
        dressItem.setRoleId(role.getCid());
        // 时装
        ItemMsgBuilder.sendItemMsg(player, ChangeType.UPDATE, dressItem, defaultItem, oldDressItem);
        // 精灵
        RoleMsgBuilder.sendRoleMsg(player, ChangeType.UPDATE, role, oldRole);
        S2CRoleMsg.Dress.Builder builder = S2CRoleMsg.Dress.newBuilder();
        MessageUtils.send(player, builder);

        // 赠送
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.DRESS);
        in.put(EventConditionKey.ROLE_CID, role.getCid());
        in.put(EventConditionKey.DRESS_ID, dressItem.getTemplateId());

        player._fireEvent(in, EEventType.TRIGGER_CHECK.value());

        Map<String, Object> info = Maps.newHashMap();
        info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.CHANGE_MODEL);
        info.put(BuildingConstant.EVENT_RESULT_DATA, role.getCid());
        player._fireEvent(info, EEventType.UNLOCK_BUILDING.value());

    }


    public boolean canDress(int[] configArrays, int dressId) {
        for (int i : configArrays) {
            if (i == dressId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算房间影响
     */
    private void calcEffect(Role role) {
        Map<Integer, Integer> effectMap = Maps.newHashMap();
        // 房间效果
        CommonUtil.changeMap(effectMap, getRoomEffect(role));
        role.setEffectMap(effectMap);
    }

    /**
     * 获取房间影响
     */
    @SuppressWarnings("rawtypes")
    private Map<Integer, Integer> getRoomEffect(Role role) {
        Map<Integer, Integer> result = Maps.newHashMap();
        if (role.getRoomId() > 0) {
            RoomItem roomItem =
                    (RoomItem) getPlayer().getBagManager().getItemCopy(role.getRoomId());
            RoomCfgBean roomCfg = GameDataManager.getRoomCfgBean(roomItem.getTemplateId());
            if (roomCfg.getEffect() == null) {
                return result;
            }
            for (Object o : roomCfg.getEffect()) {
                Map map = (Map) o;
                // 如果配置了roleId，就只对该role起作用，没有则对所有role起作用
                int roleId = ToolMap.getInt("roleId", map, 0);
                if (roleId != 0 && roleId == role.getCid() || roleId == 0) {
                    result.put((int) map.get("type"), (int) map.get("value"));
                }
            }
        }
        return result;
    }

    /** 检查重置 */
    public void checkResetCityTriggerCount() {
        RoleManager roleManager = getPlayer().getRoleManager();
        long lastTime = roleManager.getLastResetCityDatingCountTime();
        if (!TimeUtil.isSameDay(new Date(), new Date(lastTime))) {
            Map<Integer, Role> roleList = roleManager.getRoles();
            for (Role role : roleList.values()) {
                if (role.getTodayCityDatingCount() > 0) {
                    role.setTodayCityDatingCount(0);
                }
            }
            roleManager.setLastResetCityDatingCountTime(System.currentTimeMillis());
        }
    }

    public void touchRole(Player player) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ROLE_TOUCH)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:roleTouch");
        }
        boolean haveTouchTime = roleTouch.getTouchTimes() == 0 ? false : true;
        if (!haveTouchTime) {
            MessageUtils.throwCondtionError(GameErrorCode.HAVE_NO_TOUCH_TIMES, "触摸次数达到上限");
            return;
        }
        BagManager bagManager = player.getBagManager();
        boolean code = bagManager.removeItemsByTemplateIdWithCheck(
                MapUtil.of(ItemConstantId.DAILY_TOUCH_ROLE_TIMES, 1), true, EReason.TOUCH);
        if (!code) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "扣除失败");
            return;
        }
        int _oldCount = bagManager.getItemCount(ItemConstantId.ROLE_FAVOR);
        bagManager.addItem(ItemConstantId.ROLE_FAVOR, 1, true, EReason.TOUCH);
        int _newCount = bagManager.getItemCount(ItemConstantId.ROLE_FAVOR);

        org.game.protobuf.s2c.S2CRoleMsg.TouchRole.Builder builder =
                org.game.protobuf.s2c.S2CRoleMsg.TouchRole.newBuilder();
        MessageUtils.send(player, builder);
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createRoleLog(player, nowRole,
                    _newCount - _oldCount, _newCount, EReason.TOUCH.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    private void updateMood() {
        boolean isZeroMinute = DateUtil.thisMinute() == 0;
        List<Role> updateRoles = Lists.newArrayList();
        Map<Integer, Role> roles = getRoles();
        for (Entry<Integer, Role> entry : roles.entrySet()) {
            updateMood(entry.getValue());
            updateRoles.add(entry.getValue());

            if (isZeroMinute && addExtraMood(entry.getValue())) {
                updateRoles.add(entry.getValue());
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean updateMood(Role role) {
        // 半个小时自然下降心情值,写死！
        long current = System.currentTimeMillis();
        if (current - role.getLastMoodUpdateTime() < 30 * 60 * 1000) {
            return false;
        }
        role.setLastMoodUpdateTime(current);
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(role.getCid());
        List moodChangeList = cfg.getMoodChange();
        for (Object o : moodChangeList) {
            Map map = (Map) o;
            List<Integer> range = (List<Integer>) map.get("range");
            if (role.getMood() >= range.get(0) && role.getMood() <= range.get(1)) {
                int decay = (int) map.get("decay");
                if (decay < 0) {
                    decay = decay * (100 - role.getEffectByType(RoleConstant.EFFECT_TYPE_1)) / 100;
                }
                if (decay != 0) {
                    changeMood(role, decay, EReason.TOUCH);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 每小时额外增加心情值
     */
    public boolean addExtraMood(Role role) {
        int mood = role.getEffectByType(RoleConstant.EFFECT_TYPE_2);
        if (mood > 0) {
            changeMood(role, mood, EReason.TOUCH);
            return true;
        } else {
            return false;
        }
    }

    public void obtainEvent(Player player, Integer num, HeroCfgBean heroCfgBean, int dspCode) {
        Map<String, Object> in = Maps.newHashMap();
        in.put("onHeroId", heroCfgBean.getId());
        RoleCfgBean cfg = GameDataManager.getRoleCfgBean(heroCfgBean.getRole());
        // 暂未开放
        in.put("onRoleId", heroCfgBean.getRole());
        if (cfg == null || !cfg.getIsOpen()) {
            in.put("onRoleId", 0);
        }
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.HOLD_HERO);
        in.put(EventConditionKey.HERO_CID, heroCfgBean.getId());
        in.put(EventConditionKey.QUALITY, heroCfgBean.getQuality());
        player._fireEvent(in, EventType.ITEM_EVENT);
    }

    /** 刷新所有精灵状态 **/
    public void refreshRoleState() {
        timeOutState();
        if (!checkStateTime())
            return;
        roleStateTime = TimeUtil.getTodayOfhonur() + RoleConstant.REFRESH_ROLE_STATE_TIME;
        if (roles == null || roles.size() <= 0)
            return;
        roles.forEach((roleId, role) -> {
            role.getState().init(0, 0);
            refreshState(role);
            pushRoleState(role);
        });
    }

    public void timeOutState() {
        long timeNow = System.currentTimeMillis();
        roles.forEach((roleId, role) -> {
            if (role.getState().getEndtime() != 0)
                if (role.getState().getEndtime() != 0 && role.getState().getEndtime() < timeNow
                        && role.getState().getState() != 0) {
                    role.getState().init(0, 0);
                    pushRoleState(role);
                }
        });

    }

    /** 刷新某个精灵状态 **/
    public void refreshState(Role role) {
        // 饥饿
        if (hunger(role))
            return;
        // 生气
        if (angry(role))
            return;
        // 无聊
        if (boring(role))
            return;
        // 当前精灵没有状态
        role.getState().init(0, 0);

    }

    /** 饥饿 **/
    @SuppressWarnings("unchecked")
    public boolean hunger(Role role) {
        CityStateCfgBean cityState = GameDataManager.getCityStateCfgBean(RoleConstant.HUNGER);
        if (cityState == null)
            return false;

        Map<String, Integer> trigger = cityState.getTrigger();
        if (trigger == null)
            return false;
        // 条件1: n小时未赠送精灵食物
        int param1 = ToolMap.getInt(RoleConstant.STATE_CONDITION1, trigger);
        long timeNow = System.currentTimeMillis();
        // 喂食时间
        long feedTime = role.getFeedFoodTime();
        // 判断根据配置
        if (feedTime > 0 && (timeNow - feedTime) < (param1 * TimeUtil.ONE_HOUR))
            return false;
        int systemProb = ToolMap.getInt(RoleConstant.STATE_CONDITION2, trigger);
        // 条件2: n小时之后每增加n个小时,触发概率增加n
        int addTime = ToolMap.getInt(RoleConstant.STATE_CONDITION3, trigger);
        int addProb = ToolMap.getInt(RoleConstant.STATE_CONDITION4, trigger);
        // 额外获得的概率
        int extraPorb = 0;
        if (feedTime > 0)
            extraPorb = ((int) ((timeNow - feedTime) / TimeUtil.ONE_HOUR)) / addTime * addProb;
        // 条件3: 【5】看板娘每增加1（默认1）,【6】概率减少1/100
        ItemRecoverCfgBean cfgBean = getCfg(ItemConstantId.DAILY_TOUCH_ROLE_TIMES);
        int max = cfgBean == null ? 0 : cfgBean.getMaxRecoverCount();
        if (roleTouch != null && max != 0 && roleTouch.getTouchTimes() < max) {
            int systemToch = ToolMap.getInt(RoleConstant.STATE_CONDITION5, trigger);
            int addTochProb = ToolMap.getInt(RoleConstant.STATE_CONDITION6, trigger);
            extraPorb -= ((max - roleTouch.getTouchTimes()) / systemToch) * addTochProb;
        }

        if (!random(systemProb + extraPorb))
            return false;
        long endTime = (cityState.getLateTime() * TimeUtil.MINUTE) + timeNow;
        // 设置状态
        role.getState().init(endTime, RoleConstant.HUNGER);

        return true;

    }


    /** 生气 ***/
    @SuppressWarnings("unchecked")
    public boolean angry(Role role) {
        CityStateCfgBean cityState = GameDataManager.getCityStateCfgBean(RoleConstant.ANGRY);
        if (cityState == null)
            return false;
        Map<String, Integer> trigger = cityState.getTrigger();
        if (trigger == null)
            return false;
        // 条件1 :验证精灵心情值低于N
        // 心情
        int mood = role.getMood();
        int confMood = ToolMap.getInt(RoleConstant.STATE_CONDITION1, trigger);
        if (mood > confMood)
            return false;

        // 系统概率
        int systemProb = ToolMap.getInt(RoleConstant.STATE_CONDITION2, trigger);

        // 条件2:心情值在n的基础上每减少n 概率增加n
        int needMood = ToolMap.getInt(RoleConstant.STATE_CONDITION3, trigger);

        int addProb = ToolMap.getInt(RoleConstant.STATE_CONDITION4, trigger);

        int extraPorb = (confMood - mood) / needMood * addProb;
        if (extraPorb <= 0)
            extraPorb = 0;

        // 随机
        if (!random(systemProb + extraPorb))
            return false;

        // 设置状态
        long endTime = (cityState.getLateTime() * TimeUtil.MINUTE) + System.currentTimeMillis();
        role.getState().init(endTime, RoleConstant.ANGRY);

        return true;
    }

    // 无聊
    @SuppressWarnings("unchecked")
    public boolean boring(Role role) {

        CityStateCfgBean cityState = GameDataManager.getCityStateCfgBean(RoleConstant.BORING);
        if (cityState == null)
            return false;

        Map<String, Integer> trigger = cityState.getTrigger();
        if (trigger == null)
            return false;

        // 条件1:【1】n小时未和精灵日常约会

        // 系统配置时间（小时）
        int systemDailyTime = ToolMap.getInt(RoleConstant.STATE_CONDITION1, trigger);

        // 现在时间
        long timeNow = System.currentTimeMillis();

        long dailyTime = role.getDailyTime();
        // 判断时间是否满足
        if (dailyTime != 0 && (timeNow - dailyTime) < (systemDailyTime * TimeUtil.ONE_HOUR))
            return false;

        // 系统概率
        int systemProb = ToolMap.getInt(RoleConstant.STATE_CONDITION2, trigger);

        // 条件2:n小时之后每增加n个小时,触发概率增加n/100
        int needTime = ToolMap.getInt(RoleConstant.STATE_CONDITION3, trigger);

        int addProb = ToolMap.getInt(RoleConstant.STATE_CONDITION4, trigger);

        int extraProb = 0;
        if (dailyTime > 0)
            extraProb = ((int) ((timeNow - dailyTime) / TimeUtil.ONE_HOUR)) / needTime * addProb;

        ItemRecoverCfgBean cfgBean = getCfg(ItemConstantId.DAILY_TOUCH_ROLE_TIMES);
        int max = cfgBean == null ? 0 : cfgBean.getMaxRecoverCount();

        // 【5】看板娘每增加N（默认1）,【6】概率减少N
        if (roleTouch != null && max != 0 && roleTouch.getTouchTimes() < max) {
            int systemToch = ToolMap.getInt(RoleConstant.STATE_CONDITION5, trigger);
            int addTochProb = ToolMap.getInt(RoleConstant.STATE_CONDITION6, trigger);
            extraProb -= ((max - roleTouch.getTouchTimes()) / systemToch) * addTochProb;
        }

        // 随机判断是否满足条件
        if (!random(systemProb + extraProb))
            return false;

        // 设置状态
        long endTime = (cityState.getLateTime() * TimeUtil.MINUTE) + System.currentTimeMillis();
        role.getState().init(endTime, RoleConstant.BORING);
        return true;

    }

    /** 是否刷新精灵状态 **/
    public boolean checkStateTime() {
        return roleStateTime <= System.currentTimeMillis();
    }

    /** 进行随机 **/
    public boolean random(int probability) {
        // 获取随机变量
        int random = RandomUtil.randomInt(RoleConstant.SYSTEM_PROBABILITY);
        if (random > probability)
            return false;
        return true;
    }

    /** 精灵状态加成 **/
    public void addPercentByRoleState(Map<Integer, Integer> useProfit, Role role, int roleState,
            int state) {
        if (state == RoleConstant.FEED_STATE)
            role.setFeedFoodTime(System.currentTimeMillis());
        else if (state == RoleConstant.DAILY_DATING)
            role.setDailyTime(System.currentTimeMillis());
        if (role.getState().getState() != 0
                && role.getState().getEndtime() > System.currentTimeMillis()
                && role.getState().getState() == roleState) {
            calcPercent(useProfit, role);
        }
    }


    /** 计算比例 **/
    public void calcPercent(Map<Integer, Integer> useProfit, Role role) {
        CityStateCfgBean bean = GameDataManager.getCityStateCfgBean(role.getState().getState());
        if (bean == null || bean.getEffect() == null)
            return;
        useProfit.forEach((id, num) -> {
            if (bean.getEffect().containsKey(id)) {
                useProfit.put(id, num * (100 + (int) bean.getEffect().get(id)) / 100);
                role.getState().init(0, 0);
                pushRoleState(role);
            }
        });
    }

    /** 刷新状态 **/
    public void pushRoleState(Role role) {
        RoleInfo.Builder builder = RoleInfo.newBuilder();
        builder.setCt(ChangeType.UPDATE);
        builder.setId(String.valueOf(role.getCid()));
        builder.setCid(role.getCid());
        builder.setRoleState(role.getState().getState());
        MessageUtils.send(getPlayer(), builder);
    }

    public int getPreRoleId() {
        return preRoleId;
    }

    public void setPreRoleId(int preRoleId) {
        this.preRoleId = preRoleId;
    }
    
}
