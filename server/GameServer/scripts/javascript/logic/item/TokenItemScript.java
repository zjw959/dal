package javascript.logic.item;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.constant.ItemConstantId;
import logic.dungeon.scene.DatingDungeonScene;
import logic.dungeon.scene.DungeonScene;
import logic.friend.FriendManager;
import logic.hero.HeroManager;
import logic.hero.bean.Formation;
import logic.hero.bean.Hero;
import logic.item.IItemScript;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.msgBuilder.ItemMsgBuilder;
import logic.role.RoleManager;
import logic.role.bean.Role;
import logic.role.bean.RoleTouch;
import logic.support.LogicScriptsUtils;
import logic.task.TaskManager;

/** 代币类脚本 */
public class TokenItemScript extends IItemScript {

    @Override
    public boolean isAutoUse(int templateId) {
        return true;
    }

    /** 获取当前代币数量 */
    @Override
    public int getItemCount(Player player, int templateId) {
        int num = 0;
        // 此处根据templateId来确定到底是哪类代币
        switch (templateId) {
            case ItemConstantId.GOLD:
                return (int) player.getGold();
            case ItemConstantId.SYSTEM_DIAMOND:
                return (int) player.getSystemDiamond();
            case ItemConstantId.RECHARGE_DIAMOND:
                return (int) player.getRechargeDiamond();
            case ItemConstantId.FRIEND_SHIP_POINT:
                return player.getFriendManager().getFriendPoint();
            case ItemConstantId.STRENGTH:
                return player.getStrength();
            case ItemConstantId.PLAYER_EXP:
                return (int) player.getExp();
            case ItemConstantId.HERO_EXP:
                break;
            case ItemConstantId.ROLE_FAVOR:
                return player.getRoleManager().getNowRole().getFavor();
            case ItemConstantId.FORMATION_FAVOR:
                break;
            case ItemConstantId.ROLE_MOOD:
                return player.getRoleManager().getNowRole().getMood();
            case ItemConstantId.DAILY_TOUCH_ROLE_TIMES:
                RoleTouch roleTouch = player.getRoleManager().getRoleTouch();
                return roleTouch.getTouchTimes();
            case ItemConstantId.DAILY_GASHAPON_COUNT:
                return player.getCityInfoManager()
                        .getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT);
            case ItemConstantId.SKY_COIN:
                return player.getCityInfoManager().getCityPackageById(ItemConstantId.SKY_COIN);
            // 专注
            case ItemConstantId.PLAYER_ABSORBED:
                return player.getAbsorbed();
            // 魅力
            case ItemConstantId.PLAYER_GLAMOUR:
                return player.getGlamour();
            // 温柔
            case ItemConstantId.PLAYER_TENDER:
                return player.getTender();
            // 知识
            case ItemConstantId.PLAYER_KNOWLEDGE:
                return player.getKnowledge();
            // 幸运
            case ItemConstantId.PLAYER_FORTUNE:
                return player.getFortune();
            // 城市精力
            case ItemConstantId.CITY_ENERGY:
                return player.getCityInfoManager().getCityPackageById(ItemConstantId.CITY_ENERGY);
            case ItemConstantId.DAILY_DATING_COUNT:
                return player.getDatingManager().getDailyCont();
            default:
                break;
        }
        return num;
    }

    @Override
    public boolean autoUseNumEnough(Player player, int num, int templateId) {
        // 此处根据templateId来确定到底是哪类代币
        switch (templateId) {
            case ItemConstantId.GOLD:
                return player.getGold() >= num;
            case ItemConstantId.SYSTEM_DIAMOND:
                return player.getSystemDiamond() >= num;
            case ItemConstantId.RECHARGE_DIAMOND:
                return player.getRechargeDiamond() >= num;
            case ItemConstantId.FRIEND_SHIP_POINT:
                return player.getFriendManager().getFriendPoint() >= num;
            case ItemConstantId.STRENGTH:
                return player.getStrength() >= num;
            case ItemConstantId.PLAYER_EXP:
                return false;// 经验不能扣除
            case ItemConstantId.HERO_EXP:
                return false;// 经验不能扣除
            case ItemConstantId.ROLE_FAVOR:
                return false;// 经验不能扣除
            case ItemConstantId.FORMATION_FAVOR:
                // Formation formation = playerExt.getCurrentFormation();
                // Set<Role> roles = formation.getFormationProxy().getRoles();
                // for (Role role1 : roles) {
                // RoleManager.me().changeFavor(role1, num ,logDsp.clone());
                // }
                break;
            case ItemConstantId.ROLE_MOOD:
                // // 如果没有指定精灵，就加当前使用精灵
                // Role _role = role == null ? playerExt.getCurrentRole() : role;
                // RoleManager.me().changeMood(_role, num ,logDsp.clone());
                break;
            case ItemConstantId.DAILY_TOUCH_ROLE_TIMES:
                RoleTouch roleTouch = player.getRoleManager().getRoleTouch();
                return roleTouch.getTouchTimes() >= num;
            case ItemConstantId.DAILY_GASHAPON_COUNT:
                return player.getCityInfoManager()
                        .getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT) >= num;
            case ItemConstantId.SKY_COIN:
                return player.getCityInfoManager()
                        .getCityPackageById(ItemConstantId.SKY_COIN) >= num;
            // 专注
            case ItemConstantId.PLAYER_ABSORBED:
                return player.getAbsorbed() >= num;
            // 魅力
            case ItemConstantId.PLAYER_GLAMOUR:
                return player.getGlamour() >= num;
            // 温柔
            case ItemConstantId.PLAYER_TENDER:
                return player.getTender() >= num;
            // 知识
            case ItemConstantId.PLAYER_KNOWLEDGE:
                return player.getKnowledge() >= num;
            // 幸运
            case ItemConstantId.PLAYER_FORTUNE:
                return player.getFortune() >= num;
            // 城市精力
            case ItemConstantId.CITY_ENERGY:
                return player.getCityInfoManager()
                        .getCityPackageById(ItemConstantId.CITY_ENERGY) >= num;
            // 日常约会次数
            case ItemConstantId.DAILY_DATING_COUNT:
                return player.getDatingManager().getDailyCont() >= num;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean doUsebyTemplateId(Player player, int num, int templateId,
            List<Integer> customParam, Map<Integer, Integer> rewardItems, boolean isNotify,
            ItemList.Builder itemChange) {
        // 此处根据templateId来确定到底是哪类代币
        RoleManager roleManager = player.getRoleManager();
        switch (templateId) {
            case ItemConstantId.GOLD:
                long oldGold = player.getGold();
                long newGold = oldGold + num;
                // 超上限处理
                if (num > 0) {
                    if (oldGold == Long.MAX_VALUE) {
                        return true;
                    }
                    if (oldGold >= 0 && oldGold + num < 0) {
                        newGold = Long.MAX_VALUE;
                    }
                }
                // 防止负数出现
                if (newGold < 0) {
                    newGold = 0;
                }
                player.setGold(newGold);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, (int) player.getGold());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.SYSTEM_DIAMOND:
                long oldSystemDiamond = player.getSystemDiamond();
                long newSystemDiamond = oldSystemDiamond + num;
                // 超上限处理
                if (num > 0) {
                    if (oldSystemDiamond == Long.MAX_VALUE) {
                        return true;
                    }
                    if (oldSystemDiamond >= 0 && oldSystemDiamond + num < 0) {
                        newGold = Long.MAX_VALUE;
                    }
                }
                // 防止负数出现
                if (newSystemDiamond < 0) {
                    newSystemDiamond = 0;
                }
                player.setSystemDiamond(newSystemDiamond);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.SYSTEM_DIAMOND,
                            (int) player.getToltalDiamond());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.RECHARGE_DIAMOND:
                long oldRechargeDiamond = player.getRechargeDiamond();
                long newRechargeDiamond = oldRechargeDiamond + num;
                // 超上限处理
                if (num > 0) {
                    if (oldRechargeDiamond == Long.MAX_VALUE) {
                        return true;
                    }
                    if (oldRechargeDiamond >= 0 && oldRechargeDiamond + num < 0) {
                        newGold = Long.MAX_VALUE;
                    }
                }
                // 防止负数出现
                if (newRechargeDiamond < 0) {
                    newRechargeDiamond = 0;
                }
                player.setRechargeDiamond(newRechargeDiamond);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.SYSTEM_DIAMOND,
                            (int) player.getToltalDiamond());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.FRIEND_SHIP_POINT:
                FriendManager friendManager = player.getFriendManager();
                boolean change = friendManager.changeFriendPoint(num);
                if (change && isNotify) {
                    List<Item> items =
                            ItemUtils.createItems(templateId, friendManager.getFriendPoint());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.STRENGTH:
                change = player.changeStrength(num, true);
                if (change && isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getStrength());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.PLAYER_EXP:
                if (num > 0) {
                    player.addExp(num, true);
                }
                break;
            case ItemConstantId.HERO_EXP:
                // // 如果没有指定英雄，就加上阵全部英雄
                // if (hero == null) {
                // HeroManager.me().addTeamExp(player, num);
                // } else {
                // actualAddExp = HeroManager.me().addExp(num, hero);
                // changedInfo.put(id,actualAddExp);
                // }
                // 当前阵型
                Formation formation = player.getHeroManager().getFormations()
                        .get(player.getHeroManager().getFormationType());
                if (formation != null) {
                    Hero hero;
                    for (Integer hid : formation.getHeroIds()) {
                        hero = player.getHeroManager().getHero(hid);
                        // 通过非脚本类获取并执行
                        LogicScriptsUtils.getIHeroScript().addExp(player, num, hero);
                    }
                }
                break;
            case ItemConstantId.ROLE_FAVOR:
                // // 如果没有指定精灵，就加当前使用精灵
                Role _role = player.getRoleManager().getFeedRole();
                player.getRoleManager().changeFavor(_role, num, EReason.TOUCH);
                break;
            case ItemConstantId.FORMATION_FAVOR:
                HeroManager manager = player.getHeroManager();
                int type = manager.getFormationType();
                Formation formatio = manager.getFormations().get(type);
                if (formatio == null || formatio.getHeroIds().isEmpty()) {
                    break;
                }
                for (int heroId : formatio.getHeroIds()) {
                    int roleId = roleManager.getRoleCidByHeroCid(heroId);
                    Role role = roleManager.getRole(roleId);
                    roleManager.changeFavor(role, num, EReason.DUNGEON_DUNGEON_PASS);
                }
                break;
            // 对关卡约会特定精灵的奖励
            case ItemConstantId.ROLE_DUNGEON_DATING_FAVOR:
                DungeonScene dungeonScene = player.getDungeonManager().getCurrentScene();
                if (dungeonScene == null || !(dungeonScene instanceof DatingDungeonScene))
                    break;
                DatingDungeonScene datingScene = (DatingDungeonScene) dungeonScene;
                if (datingScene.getCurrentDatingBean() == null)
                    break;
                DatingRuleCfgBean ruleCfg = GameDataManager
                        .getDatingRuleCfgBean(datingScene.getCurrentDatingBean().getScriptId());
                if (ruleCfg == null)
                    break;
                Role datingRole = player.getRoleManager().getRole(ruleCfg.getDungeonRoleId());
                if (datingRole == null)
                    break;
                player.getRoleManager().changeFavor(datingRole, num, EReason.DUNGEON_DUNGEON_PASS);
                break;
            case ItemConstantId.ROLE_MOOD:
                // 看板娘
                Role role = roleManager.getFeedRole();
                roleManager.changeMood(role, num, EReason.DATING_BEGIN);
                break;
            case ItemConstantId.DAILY_TOUCH_ROLE_TIMES:
                RoleTouch roleTouch = player.getRoleManager().getRoleTouch();
                if (roleTouch == null) {
                    roleTouch = new RoleTouch();
                    roleTouch.setLastRecoverTime(new Date());
                }
                roleTouch.change(num);
                player.getRoleManager().setRoleTouch(roleTouch);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.DAILY_TOUCH_ROLE_TIMES,
                            roleTouch.getTouchTimes());
                    if (items.isEmpty()) {
                        break;
                    }
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.PLAYER_ACTIVE:
                TaskManager taskManager = player.getTaskManager();
                int current = taskManager.addActEngrty(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, current);
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.DAILY_GASHAPON_COUNT:
                player.getCityInfoManager().addCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT,
                        num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.DAILY_GASHAPON_COUNT,
                            player.getCityInfoManager()
                                    .getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT));
                    if (items.isEmpty()) {
                        break;
                    }
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.SKY_COIN:
                player.getCityInfoManager().addCityPackageById(ItemConstantId.SKY_COIN, num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.SKY_COIN, player
                            .getCityInfoManager().getCityPackageById(ItemConstantId.SKY_COIN));
                    if (items.isEmpty()) {
                        break;
                    }
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            // 专注
            case ItemConstantId.PLAYER_ABSORBED:
                player.changeAbsorbed(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getAbsorbed());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            // 魅力
            case ItemConstantId.PLAYER_GLAMOUR:
                player.changeGlamour(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getGlamour());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            // 温柔
            case ItemConstantId.PLAYER_TENDER:
                player.changeTender(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getTender());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            // 知识
            case ItemConstantId.PLAYER_KNOWLEDGE:
                player.changeKnowledge(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getKnowledge());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            // 幸运
            case ItemConstantId.PLAYER_FORTUNE:
                player.changefortune(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(templateId, player.getFortune());
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.CITY_ENERGY:
                player.getCityInfoManager().changeCityEnergy(num, true);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.CITY_ENERGY, player
                            .getCityInfoManager().getCityPackageById(ItemConstantId.CITY_ENERGY));
                    if (items.isEmpty()) {
                        break;
                    }
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            case ItemConstantId.DAILY_DATING_COUNT:
                player.getDatingManager().changeDailyCont(num);
                if (isNotify) {
                    List<Item> items = ItemUtils.createItems(ItemConstantId.DAILY_DATING_COUNT,
                            player.getDatingManager().getDailyCont());
                    if (items.isEmpty()) {
                        break;
                    }
                    ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
                }
                break;
            default:
                break;
        }
        // 事件（特殊处理）
        if (templateId == ItemConstantId.GOLD && num <= 0) {
            return true;
        }
        obtainEvent(player, num, templateId, 0);
        return true;
    }

    @Override
    public boolean doUsebyUid(Player player, int num, long uid, List<Integer> customParam,
            Map<Integer, Integer> rewardItems, boolean isNotify, ItemList.Builder itemMsg) {
        return true;
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ITEM_TOKEN.Value();
    }

    private static void obtainEvent(Player player, Integer num, int templateId, int dspCode) {
        BaseGoods baseGoods = GameDataManager.getBaseGoods(templateId);
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.ADD_ITEM);
        in.put(EventConditionKey.ITEM_CID, baseGoods.getId());
        in.put(EventConditionKey.SUPER_TYPE, baseGoods.getSuperType());
        in.put(EventConditionKey.SUB_TYPE, baseGoods.getSubType());
        // in.put(EventConditionKey.SMALL_TYPE, baseGoods.getSmallType());
        in.put(EventConditionKey.COUNT, num);
        in.put(EventConditionKey.EXCLUDE, dspCode);
        // Event event = new Event(EventType.ITEM_EVENT, in);
        player._fireEvent(in, EventType.ITEM_EVENT);
    }
}
