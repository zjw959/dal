package javascript.logic.info;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import logic.character.bean.Player;
import logic.city.build.BuildingConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EAcrossDayType;
import logic.constant.EAntiAddictionType;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.ItemConstantId;
import logic.functionSwitch.FunctionSwitchService;
import logic.info.IInfoManagerScript;
import logic.info.InfoManager;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.mail.MailService;
import logic.msgBuilder.ItemMsgBuilder;
import logic.msgBuilder.PlayerMsgBuilder;
import logic.support.MessageUtils;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CPlayerMsg.ResAntiAddictionInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import utils.ChannelUtils;
import utils.DateEx;
import utils.ToolMap;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.ItemRecoverCfgBean;

public class InfoManagerScript implements IInfoManagerScript {
    private static final Logger LOGGER = Logger.getLogger(InfoManagerScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.INFO_MANAGER_SCRIPT.Value();
    }

    @Override
    public boolean isAnti(InfoManager im) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ANTI_ADDICTION)) {
            return false;
        }
        if (im.getAntiStatus() == EAntiAddictionType.UNCERTIFIED.value()
                || im.getAntiStatus() == EAntiAddictionType.MINOR.value()) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ANTI_ADDICTION);
            int limitHour = ToolMap.getInt("hours", cfg.getData(), 4);
            if (im.getTodayOnlineTime() >= limitHour * InfoManager.UNIT_TIME) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 发送防沉迷信息
     */
    private void sendAntiAddiction(Player player, int hour) {
        if (ChannelUtils.isDisconnectChannel(player.getCtx())) {
            return;
        }
        InfoManager im = player.getInfoManager();
        ResAntiAddictionInfo.Builder builder =
                PlayerMsgBuilder.buildAntiAddictionMsg(im.getAntiStatus(), hour);
        MessageUtils.send(player, builder);
        LOGGER.info("anti-addiction, sendAntiAddiction hour:" + hour + " playerId:"
                + player.getPlayerId() + " antiStatus:" + im.getAntiStatus());
    }
    
    @Override
    public void tick(Player player) {
        InfoManager im = player.getInfoManager();
        long now = System.currentTimeMillis();
        if (im.getLastTickTime() > 0) {
            long dis = now - im.getLastTickTime();
            im.setTodayOnlineTime(im.getTodayOnlineTime() + dis);
            if (im.getAntiStatus() == EAntiAddictionType.MINOR.value()
                    || im.getAntiStatus() == EAntiAddictionType.UNCERTIFIED.value()) {
                if (now > im.getNextAntiTime()) {
                    long h = im.getTodayOnlineTime() / InfoManager.UNIT_TIME;
                    im.setNextAntiTime(now + ((h + 1) * InfoManager.UNIT_TIME - im.getTodayOnlineTime()));// 下次防沉迷通知时间
                    // 功能是否关闭
                    if (FunctionSwitchService.getInstance().isOpenFunction(
                            EFunctionType.ANTI_ADDICTION)) {
                        int hour = (int) (im.getTodayOnlineTime() / InfoManager.UNIT_TIME);
                        DiscreteDataCfgBean cfg =
                                GameDataManager
                                        .getDiscreteDataCfgBean(DiscreteDataID.ANTI_ADDICTION);
                        int limitHour = ToolMap.getInt("hours", cfg.getData(), 4);
                        if (hour >= 1 && hour <= limitHour) {
                            sendAntiAddiction(player, hour);
                        }
                    }
                }
            }
        }
        im.setLastTickTime(now);
        strengthTick(im);
    }

    @Override
    public boolean changeStrength(InfoManager im, int num, boolean isForce) {
        // 当前等级体力上限
        int levelMax = im.getLevelMaxStrength();
        int max = levelMax;
        if (isForce) {
            max = GameDataManager.getBaseGoods(ItemConstantId.STRENGTH).getTotalMax();
        } else {
            if (im.getStrength() >= max) {
                return false;
            }
        }
        int total = im.getStrength() + num;
        if (total < 0) {
            if (num > 0) {// 超上限
                total = max;
            } else {
                total = 0;
            }
        }
        if (total > max) {
            total = max;
        }
        if (total != im.getStrength()) {
            im.setStrength(total);
            if (im.getStrength() >= levelMax) {// 体力达到当前等级上限，就把上次恢复时间置0
                im.setLastRecoverStrengthTime(0);
            } else {
                if (im.getLastRecoverStrengthTime() == 0) {// 体力未满，且体力之前是饱和状态时，重置上次恢复时间为当前时间
                    im.setLastRecoverStrengthTime(System.currentTimeMillis());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 体力检查恢复
     */
    private void strengthTick(InfoManager im) {
        if (im.getLastRecoverStrengthTime() == 0) {
            return;
        }
        int max = im.getLevelMaxStrength();// 当前等级体力上限
        int now = im.getStrength();
        if (now >= max) {
            return;
        }
        ItemRecoverCfgBean bean = getStrengthRecoverCfg();
        long nowTime = System.currentTimeMillis();
        long _time = nowTime - im.getLastRecoverStrengthTime();
        if (_time >= (bean.getCooldown() * DateEx.TIME_SECOND)) {
            int _times = (int) (_time / (bean.getCooldown() * DateEx.TIME_SECOND));
            int _recover = _times * bean.getRecoverCount();
            boolean change = im.changeStrength(_recover, false);
            if (change) {
                im.sendStrengthUpdate();
            }
            im.setLastRecoverStrengthTime(nowTime);
        }
        im.setRecoverStrength(im.getRecoverStrength() + im.getStrength() - now);
    }

    @Override
    public int getLevelMaxStrength(Player player) {
        int max = GameDataManager.getLevelUpCfgBean(player.getLevel()).getMaxEnergy();
        return max;
    }

    /**
     * 获取体力恢复的配置
     * 
     * @return
     */
    private ItemRecoverCfgBean getStrengthRecoverCfg() {
        List<ItemRecoverCfgBean> list = GameDataManager.getItemRecoverCfgBeans();
        for (ItemRecoverCfgBean bean : list) {
            if (bean.getItemId() == ItemConstantId.STRENGTH) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public void sendStrengthUpdate(Player player) {
        List<Item> items = ItemUtils.createItems(ItemConstantId.STRENGTH, player.getStrength());
        ItemList.Builder itemChange = ItemList.newBuilder();
        ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
        MessageUtils.send(player, itemChange);
    }

    @Override
    public void changeAbsorbed(InfoManager im, int change) {
        im.setAbsorbed(calculate(im, im.getAbsorbed(), change,
                getMaxByItemId(ItemConstantId.PLAYER_ABSORBED)));
    }

    @Override
    public void changeGlamour(InfoManager im, int change) {
        im.setGlamour(calculate(im, im.getGlamour(), change,
                getMaxByItemId(ItemConstantId.PLAYER_GLAMOUR)));
    }

    @Override
    public void changeTender(InfoManager im, int change) {
        im.setTender(calculate(im, im.getTender(), change,
                getMaxByItemId(ItemConstantId.PLAYER_TENDER)));
    }

    @Override
    public void changeKnowledge(InfoManager im, int change) {
        im.setKnowledge(calculate(im, im.getKnowledge(), change,
                getMaxByItemId(ItemConstantId.PLAYER_KNOWLEDGE)));
    }

    @Override
    public void changefortune(InfoManager im, int change) {
        im.setFortune(calculate(im, im.getFortune(), change,
                getMaxByItemId(ItemConstantId.PLAYER_FORTUNE)));
    }

    @Override
    public int getMaxByItemId(int itemId) {
        return GameDataManager.getItemCfgBean(itemId).getTotalMax();
    }

    @Override
    public int calculate(InfoManager im, int parm, int change, int max) {
        // 有变化则创建事件
        im.createEvent();
        if (change > 0) {
            if ((parm + change) >= max)
                return max;
            return parm + change;
        } else {
            if ((parm + change) <= Integer.MIN_VALUE)
                return 0;
            return parm + change;
        }
    }

    @Override
    public void createEvent(Player player) {
        Map<String, Object> info = Maps.newHashMap();
        info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.ATTRIBUTE_CHANGE);
        player._fireEvent(info, EEventType.UNLOCK_BUILDING.value());
    }

    @Override
    public void acrossDay(Player player, EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.SYS_ACROSS_DAY) {
            player.getInfoManager().setTodayOnlineTime(0);
        }
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            // 这里依赖于时钟框架先执行acrossDay()后进行setGameAcrossDay()的流程
            // 这里这样设置是为了强行设置时间后对数据的计算支持
            handleOverflowStrenth(player, System.currentTimeMillis());
        }
    }

    @Override
    public void createPlayerInitialize(Player player) {
        // 不进行加载时结算体力溢出，这时候会造成重复结算
    }

    /**
     * 处理溢出体力值
     * <p>
     * 角色加载时计算未登录数据,加载后时钟调用的先后顺序会使计算数据不同步
     */
    private void handleOverflowStrenth(Player player, long nowTime) {
        InfoManager im = player.getInfoManager();
        ItemRecoverCfgBean bean = getStrengthRecoverCfg();
        // 获取当天跨天时间
        long todayAcross = im.getGameAcrossDay();
        // 满体力时lastRecoverStrengthTime=0因此不需要进行体力的恢复计算
        long defaultBegin =
                im.getLastRecoverStrengthTime() == 0 ? todayAcross : im
                        .getLastRecoverStrengthTime();
        AtomicInteger tempStrength = new AtomicInteger(im.getStrength());
        // 是否存在离线跨天
        int count = 0;
        int offlineDay = 0;
        if (nowTime - todayAcross >= 0) {
            // 上次跨天应该恢复的体力
            count +=
                    calculateDuelBrandCount(player, im, defaultBegin, todayAcross, bean,
                            offlineDay, tempStrength);
        }
        // 整天处理
        long nextAcross = im.getGameNextAcrossDay(new Date(todayAcross));
        while (nowTime > nextAcross) {
            offlineDay++;
            count +=
                    calculateDuelBrandCount(player, im, todayAcross, nextAcross, bean, offlineDay,
                            tempStrength);
            todayAcross = nextAcross;
            nextAcross = im.getGameNextAcrossDay(new Date(todayAcross));
        }
        sendDuelBrandMail(player, count);
    }

    private int calculateDuelBrandCount(Player player, InfoManager im, long startTime,
            long endTime, ItemRecoverCfgBean bean, int offlineDay, AtomicInteger tempStrength) {
        int max = im.getLevelMaxStrength();
        // 恢复值计算
        long _time = endTime - startTime;
        int _times = (int) (_time / (bean.getCooldown() * DateEx.TIME_SECOND));
        int _recover = _times * bean.getRecoverCount();
        int needRecover = max - tempStrength.get();
        if (needRecover > 0) {
            int addition = Math.min(needRecover, _recover);
            im.setRecoverStrength(im.getRecoverStrength() + addition);
            tempStrength.set(tempStrength.get() + addition);
        }
        // 处理，置零
        int count = calculateDuelBrandCount(bean, offlineDay, im.getRecoverStrength());
        im.setRecoverStrength(0);
        return count;
    }

    @SuppressWarnings("rawtypes")
    private int calculateDuelBrandCount(ItemRecoverCfgBean bean, int offlineDay, int recoverStrength) {
        Map data = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MULTIPLE_REWARD).getData();
        int maxOffline = (Integer) data.get(DiscreteDataKey.MULTIPLE_REWARD_MAX_OFFLINE);
        double offlineBase =
                ((BigDecimal) data.get(DiscreteDataKey.MULTIPLE_REWARD_SEQ_OFFLINE)).doubleValue();
        int unitStrength = (Integer) data.get(DiscreteDataKey.MULTIPLE_REWARD_UNIT_ENERGY);
        // 道具数量=(每日理论恢复体力-当日实际恢复体力)/30*〖0.845〗^连续未登录天数
        int dayRecover = (int) (DateEx.TIME_DAY / (bean.getCooldown() * DateEx.TIME_SECOND));
        double offlineShift =
                (Math.pow(offlineBase, offlineDay > maxOffline ? maxOffline : offlineDay));
        return (int) ((dayRecover - recoverStrength) * offlineShift / unitStrength);
    }

    private void sendDuelBrandMail(Player player, int count) {
        if (count > 0) {
            // 邮件文本
            String title = GameDataManager.getStringCfgBean(212013).getText();
            String body = GameDataManager.getStringCfgBean(212014).getText();
            Map<Integer, Integer> duels = new HashMap<Integer, Integer>(1);
            duels.put(ItemConstantId.DUEL_BRAND, count);
            MailService.getInstance().sendPlayerMail(false, player.getPlayerId(), title, body,
                    duels, EReason.ITEM_DUEL_BRAND);
        }
    }
}
