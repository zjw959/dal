package javascript.logic.backpack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.bag.BagManager;
import logic.bag.IBagScript;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.log.bean.ItemGenLog;
import logic.log.bean.ItemLog;
import logic.mail.MailService;
import logic.msgBuilder.ItemMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CItemMsg.ItemList.Builder;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import server.GameServer;
import thread.log.LogProcessor;
import utils.CommonUtil;
import utils.ExceptionEx;
import utils.IdGenerator;
import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.HeroCfgBean;
import data.bean.MedalCfgBean;

public class BagScript extends IBagScript {
    private static final Logger LOGGER = Logger.getLogger(BagScript.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> addItem(BagManager bag, Player player, int templateId, int num,
            boolean isNotify, EReason reason, boolean needMail, String... ext) {
        List<Item> items = ItemUtils.createItems(templateId, num);
        if (items.isEmpty()) {
            // 一旦有一个道具创建失败. 所有失败
            return Collections.EMPTY_LIST;
        }
        // 用于记录进入背包的道具 不包括自使用的道具
        List<Item> updates = new ArrayList<>();
        // 返回 所有创建的道具 包括自使用的
        items = _add(player, bag, items, updates, reason, isNotify, needMail, ext);
        return items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> addItem(BagManager bag, Player player, Map<Integer, Integer> templateIdNums,
            boolean isNotify, EReason reason, boolean needMail, String... ext) {
        if (templateIdNums == null || templateIdNums.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        // / 存放单个模板创建的道具
        List<Item> singleCreates = new ArrayList<>();
        // 存放全部创建的道具
        List<Item> createList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> idNums : templateIdNums.entrySet()) {
            singleCreates = ItemUtils.createItems(idNums.getKey(), idNums.getValue());
            if (singleCreates.isEmpty()) {
                return Collections.EMPTY_LIST; // 一旦有一个道具创建失败. 所有失败
            }
            // 防止策划道具配置错误 先注释不用
            // if (wrapper.getMaxNum() > 1) {
            // _createItem = items.get(0);
            // createList.add(_createItem);
            // } else {
            // createList.addAll(items);
            // }
            createList.addAll(singleCreates);
        }
        List<Item> updates = new ArrayList<>();
        createList = _add(player, bag, createList, updates, reason, isNotify, needMail, ext);
        return createList;
    }

    /**
     * 物品入背包的必经入口
     * 
     * @return 返回所有新增的道具 包括自使用 的道具
     */
    @SuppressWarnings("unchecked")
    private List<Item> _add(Player player, BagManager bag, List<Item> items, List<Item> updates,
            EReason reason, boolean isNotify, boolean needMail, String... ext) {
        boolean fromMail = false;// 是否来自邮件领取奖励
        if (ext != null && ext.length > 1 && ext[0].equals(EReason.ITEM_REWARD_MAIL.name())) {
            fromMail = true;
        }
        ItemList.Builder itemChange = ItemList.newBuilder();
        List<Item> ret = new ArrayList<>();
        List<Item.ItemLogBean> adds = new ArrayList<>();
        // 转换策略（溢出的走转换策略）
        Map<Integer, Integer> convertStrategy = new HashMap<Integer, Integer>();
        // 邮件策略（背包满的走邮件）
        Map<Integer, Integer> mailStrategy = new HashMap<Integer, Integer>();
        BagManager bagManager = player.getBagManager();
        for (Item item : items) {
            int _itemTempId = item.getTemplateId();
            int num = item.getNum();
            if (num <= 0) {
                continue;
            }
            BaseGoods baseGoods = GameDataManager.getBaseGoods(_itemTempId);
            int bagType = baseGoods.getBagType();
            // 是否具有转换策略
            boolean isConvert = baseGoods.isConvert();
            // 空闲空间
            int freeCapacity = bagManager.getFreeCapacity(bagType);
            // 最大数量
            int totalMax = baseGoods.getTotalMax();
            if (totalMax <= 0) {// 不设上限
                totalMax = Integer.MAX_VALUE;
            }
            // 溢出数量
            int overflowNum = 0;
            Map<Integer, Integer> srcConvertMap = null;
            // （1）优先处理转换策略
            if (baseGoods instanceof HeroCfgBean) {// 精灵特殊处理
                isConvert = true;// 精灵必然有转换策略，但之前的配置表结构不好读取，此处写死
                HeroCfgBean heroCfg = (HeroCfgBean) baseGoods;
                totalMax = 1;
                // 判定玩家角色有无此精灵，有则做溢出处理
                boolean exist = player.getHeroManager().isExistHero(heroCfg.getId());
                overflowNum = num;
                if (!exist) {
                    // 一个转化为精灵，其余转化为碎片
                    overflowNum = num - 1;
                    num = 0;// 数量清零，避免后面的逻辑再次处理
                    // 添加一个精灵
                    player.getHeroManager().addHero(heroCfg, reason);
                    adds.add(item.toLogBean(item.getNum(), 0, bagManager.getItemCount(_itemTempId),
                            fromMail));
                }
                srcConvertMap = heroCfg.getConvert();
            } else if (baseGoods instanceof MedalCfgBean) {
                isConvert = false;
                MedalCfgBean medalCfg = (MedalCfgBean) baseGoods;
                totalMax = 1;
                num = 0;// 数量清零，避免后面的逻辑再次处理
                // 添加勋章
                player.getMedalManager().addMedal(medalCfg);
                adds.add(item.toLogBean(item.getNum(), 0, bagManager.getItemCount(_itemTempId),
                        fromMail));
            } else {
                if (isConvert) {
                    // 其他道具都按达到上限后进行转换处理
                    srcConvertMap = baseGoods.getConvertMax();
                }
                overflowNum = bagManager.getItemCount(_itemTempId) + num - totalMax;
            }
            // 处理转换道具
            Map<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
            if (overflowNum > 0 && srcConvertMap != null) {
                num -= overflowNum;
                convertStrategy(srcConvertMap, overflowNum, convertStrategy);
                if (convertStrategy.size() > 0) {
                    Iterator<Entry<Integer, Integer>> it = srcConvertMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<Integer, Integer> entry = it.next();
                        CommonUtil.changeMap(itemMap, entry.getKey(), entry.getValue());
                    }
                }
            }
            // 重新整合后的道具集合
            List<Item> convertItems = new ArrayList<Item>();
            if (num > 0) {// 修正未转换的道具数量并整合
                item.setNum(num);
                convertItems.add(item);
            }
            for (Entry<Integer, Integer> entry : itemMap.entrySet()) {
                convertItems.addAll(ItemUtils.createItems(entry.getKey(), entry.getValue()));
            }
            // （2）用转换之后重新整合的道具集合真正添加到背包
            for (Item _item : convertItems) {
                _itemTempId = _item.getTemplateId();
                int _num = _item.getNum();
                // 1. 处理爆仓
                // 先处理超过本身道具最大获得上限部分
                BaseGoods goods = GameDataManager.getBaseGoods(_itemTempId);
                totalMax = goods.getTotalMax();
                overflowNum = bagManager.getItemCount(_itemTempId) + _num - totalMax;
                freeCapacity = bagManager.getFreeCapacity(bagType);

                if (overflowNum > 0) {
                    _num -= overflowNum;
                    _item.setNum(_num);
                    mailStrategy(_itemTempId, overflowNum, mailStrategy);
                    adds.add(_item.toLogBean(overflowNum, 0, bagManager.getItemCount(_itemTempId),
                            true));
                }
                if (_item.getNum() <= 0) {
                    continue;
                }
                // 优先处理自动使用道具（主要是代币，因为代币不占用背包格子）
                if (ItemUtils.isAutoUse(_itemTempId)) {
                    ItemUtils.doUsebyTemplateId(player, _item.getNum(), _itemTempId, null, null,
                            isNotify, itemChange);
                    ret.add(_item);
                    adds.add(_item.toLogBean(_item.getNum(), 0,
                            bagManager.getItemCount(_itemTempId), fromMail));
                    continue;
                }
                // 再处理超出背包容量部分
                if (!ItemUtils.isStackable(_itemTempId)) {
                    // 爆仓策略,溢出部分丢邮件
                    if (freeCapacity < _item.getNum()) {
                        mailStrategy(_itemTempId, _item.getNum() - freeCapacity, mailStrategy);
                        adds.add(_item.toLogBean(_item.getNum() - freeCapacity, 0,
                                bagManager.getItemCount(_itemTempId), true));
                        // 重置添加数量
                        _item.setNum(freeCapacity);
                    }
                } else {
                    List<Item> currentItems = bag.getItemCopyByTemplateId(_itemTempId);
                    if (currentItems.isEmpty()) {
                        // 爆仓策略,溢出部分丢邮件
                        if (freeCapacity < 1) {
                            mailStrategy(_itemTempId, _item.getNum(), mailStrategy);
                            adds.add(_item.toLogBean(_item.getNum(), 0,
                                    bagManager.getItemCount(_itemTempId), true));
                            // 重置添加数量
                            _item.setNum(0);
                        }
                    }
                }
                // 2. 真正入背包
                if (_item.getNum() > 0) {
                    boolean _isExist = _isExist(bag, _itemTempId);
                    if (!ItemUtils.isStackable(goods)) {
                        if (_item.getNum() > 1) {
                            // 正常情况下不会出现，因为创建道具时不可堆叠的道具都是一个一个创建的
                            LOGGER.error(
                                    ConstDefine.LOG_ERROR_LOGIC_PREFIX + "add item num err, itemId:"
                                            + _itemTempId + " num:" + _item.getNum() + "\n"
                                            + ExceptionEx.currentThreadTracesWithOutMineLine());
                        }
                        _putItem(bag, _item);
                        ret.add(_item);
                        adds.add(_item.toLogBean(_item.getNum(), 0,
                                bagManager.getItemCount(_itemTempId), fromMail));
                        if (ItemUtils.recoredNew(_itemTempId)) {
                            _addNewItemId(bag, _item.getId());
                        }
                        ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, _item);
                    } else {
                        List<Item> currentItems = bag.getItemCopyByTemplateId(_itemTempId);
                        if (currentItems.isEmpty()) { // 新建道具
                            if (ItemUtils.recoredNew(_itemTempId)) {
                                _addNewItemId(bag, _item.getId());
                            }
                            ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, _item);
                            _putItem(bag, _item);
                        } else {
                            // 和现有道具合并
                            Item old = currentItems.get(0);
                            old.setNum(old.getNum() + _item.getNum());
                            ItemMsgBuilder.addItemMsg(itemChange, ChangeType.UPDATE, old);
                            updates.add(old);
                            _item.setId(old.getId());
                            _putItem(bag, old);
                        }
                        ret.add(_item);
                        adds.add(_item.toLogBean(_item.getNum(), 0,
                                bagManager.getItemCount(_itemTempId), fromMail));
                    }
                    ItemUtils.putTrigger(player, _itemTempId, _isExist);
                }
            }
        }
        // 处理走邮件的道具
        if (needMail && mailStrategy.size() > 0) {
            String title = GameDataManager.getStringCfgBean(212003).getText();
            String body = GameDataManager.getStringCfgBean(212004).getText();
            ((MailService) (MailService.getInstance())).sendPlayerMail(false, player.getPlayerId(),
                    title, body, mailStrategy, reason);
        }
        if (isNotify) {
            notifyItemUpdate(player, itemChange);
        }
        // 道具获得日志
        logItemGain(adds, player, reason, ext);
        return ret;
    }

    /**
     * 道具溢出的转化策略
     * 
     * @param srcConvertMap
     * @param overflowNum
     * @param resultMap
     */
    private void convertStrategy(Map<Integer, Integer> srcConvertMap, int overflowNum,
            Map<Integer, Integer> resultMap) {
        if (overflowNum <= 0) {
            return;
        }
        Iterator<Entry<Integer, Integer>> it = srcConvertMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, Integer> entry = it.next();
            int addNum = entry.getValue() * overflowNum;
            int newNum = addNum;
            Integer oldNum = resultMap.get(entry.getKey());
            if (oldNum != null) {
                newNum += oldNum.intValue();
            }
            resultMap.put(entry.getKey(), newNum);
        }
    }

    /**
     * 道具溢出的丢邮件策略
     * 
     * @param srcConvertMap
     * @param overflowNum
     * @param resultMap
     */
    private void mailStrategy(int templateId, int num, Map<Integer, Integer> resultMap) {
        if (num <= 0) {
            return;
        }
        int newNum = num;
        Integer oldNum = resultMap.get(templateId);
        if (oldNum != null) {
            newNum += oldNum.intValue();
        }
        resultMap.put(templateId, newNum);
    }

    private void logItemGain(List<Item.ItemLogBean> items, Player player, EReason reason,
            String... ext) {
        if (items.isEmpty())
            return;
        if (GameServer.getInstance().isRootDrangServer()
                && (reason == EReason.ITEM_GM || reason == EReason.MAIL_GM)) {
            return;
        }
        String extInfo = "";
        if (ext != null) {
            for (int i = 0; i < ext.length; i++) {
                extInfo += ext[i] + " ";
            }
        }
        for (Item.ItemLogBean item : items) {
            try {
                ItemGenLog bean = LogBeanFactory.createItemGainLog(player, item.getUid(),
                                item.getTemplateId(), item.getGainNum(), item.getTotalNum(),
                                reason.value(), item.isFromMail(),
                        extInfo);
                LogProcessor.getInstance().sendLog(bean);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    public boolean removeItem(long id, int num, Player player, BagManager bag, boolean isNotify,
            EReason reason, String... ext) {
        List<Item.ItemLogBean> used = new ArrayList<>();
        ItemList.Builder itemChange = ItemList.newBuilder();
        boolean ret = _removeById(bag, id, num, itemChange, used);
        if (isNotify) {
            notifyItemUpdate(player, itemChange);
        }
        if (ret) {
            logItemConsume(used, player, reason, IdGenerator.getLogId(), ext);
        }
        return ret;
    }


    @Override
    public boolean removeItemByTemplateId(BagManager bag, Player player, int templateId, int num,
            boolean isNotify, EReason reason, String... ext) {
        ItemList.Builder itemChange = ItemList.newBuilder();
        List<Item.ItemLogBean> used = new ArrayList<>();
        boolean ret = true;
        // 1. 是否是移除自使用道具出错
        if (ItemUtils.isAutoUse(templateId)) {
            if (!ItemUtils.autoUseNumEnough(player, num, templateId)) {
                return false;
            }
            ret = ItemUtils.doUsebyTemplateId(player, -num, templateId, null, null, isNotify,
                    itemChange);
            _logItemConsume(templateId, num, player, reason, IdGenerator.getLogId(), ext);
        } else {
            ret = _removeByTemplateId(bag, templateId, num, itemChange, used);
            if (isNotify) {
                notifyItemUpdate(player, itemChange);
            }
            if (ret) {
                logItemConsume(used, player, reason, IdGenerator.getLogId(), ext);
            }
        }
        return ret;
    }


    @Override
    public boolean removeItemsById(BagManager bag, Player player, Map<Long, Integer> idNums,
            boolean isNotify, EReason reason, String... ext) {
        if (!isEnoughByIds0(bag, idNums)) {
            return false;
        }
        ItemList.Builder itemMsg = ItemList.newBuilder();
        List<Item.ItemLogBean> used = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : idNums.entrySet()) {
            _removeById(bag, entry.getKey(), entry.getValue(), itemMsg, used);
        }
        if (isNotify) {
            notifyItemUpdate(player, itemMsg);
        }
        logItemConsume(used, player, reason, IdGenerator.getLogId(), ext);
        return true;
    }

    @Deprecated
    @Override
    public void removeUnUsdItem(BagManager bagManager, Player player, boolean isNotify,
            EReason reason) {
        ItemList.Builder itemMsg = ItemList.newBuilder();
        List<Item.ItemLogBean> used = new ArrayList<>();

        if (!GameServer.getInstance().isTestServer()) {
            LOGGER.error(
                    "正式服调用 removeUnUsedItems!!!!!!!!!!!!!" + ExceptionEx.currentThreadTraces());
            return;
        }

        List<Item> items = bagManager.getAllItemsByItemsKVNoCopy();
        Map<Long, Integer> idNums = new HashMap<Long, Integer>();
        for (Item item : items) {
            if (!item.isInUse()) {
                idNums.put(item.getId(), item.getNum());
            }
        }

        for (Map.Entry<Long, Integer> entry : idNums.entrySet()) {
            _removeById(bagManager, entry.getKey(), entry.getValue(), itemMsg, used);
        }
        if (isNotify) {
            notifyItemUpdate(player, itemMsg);
        }
        logItemConsume(used, player, reason, IdGenerator.getLogId());
    }

    @Override
    public boolean removeItemsByTemplateId(BagManager bag, Player player,
            Map<Integer, Integer> templateIdNums, boolean isNotify, EReason reason,
            boolean needCheck, String... ext) {
        if (needCheck && !_isEnoughByTemplateIds(player, bag, templateIdNums)) {
            return false;
        }
        ItemList.Builder itemChange = ItemList.newBuilder();
        List<Item.ItemLogBean> used = new ArrayList<>();
        boolean ret = true;
        long logId = IdGenerator.getLogId();
        for (Map.Entry<Integer, Integer> entry : templateIdNums.entrySet()) {
            if (ItemUtils.isAutoUse(entry.getKey())) {
                if (!ItemUtils.doUsebyTemplateId(player, -entry.getValue(), entry.getKey(), null,
                        null, isNotify, itemChange)) {
                    ret = false;
                    LOGGER.warn("remove item list , item are not enough. reason :" + reason.value()
                            + "tempId:" + entry.getKey() + " playerId:" + player.getPlayerId()
                            + ExceptionEx.currentThreadTraces());
                    break;
                }
                _logItemConsume(entry.getKey(), entry.getValue(), player, reason, logId, ext);
                continue;
            }
            if (!_removeByTemplateId(bag, entry.getKey(), entry.getValue(), itemChange, used)) {
                ret = false;
                LOGGER.warn("remove item list , item are not enough. reason :" + reason.value()
                        + "tempId:" + entry.getKey() + " playerId:" + player.getPlayerId()
                        + ExceptionEx.currentThreadTraces());
                break;
            }
        }

        if (isNotify) {
            notifyItemUpdate(player, itemChange);
        }
        logItemConsume(used, player, reason, logId, ext);
        return ret;
    }


    private boolean isEnoughByIds0(BagManager bag, Map<Long, Integer> combine) {
        for (Map.Entry<Long, Integer> entry : combine.entrySet()) {
            if (!_enoughById(bag, entry.getKey(), entry.getValue())) {
                return false; // 包含一种道具不足 直接失败
            }
        }
        return true;
    }

    /**
     * 道具是否足够
     * 
     * @param player
     * @param bag
     * @param templateIdNums
     * @return
     */
    private boolean _isEnoughByTemplateIds(Player player, BagManager bag,
            Map<Integer, Integer> templateIdNums) {
        for (Map.Entry<Integer, Integer> entry : templateIdNums.entrySet()) {
            if (ItemUtils.isAutoUse(entry.getKey())) {
                if (!ItemUtils.autoUseNumEnough(player, entry.getValue(), entry.getKey())) {
                    return false;
                }
                continue;
            }
            if (!_enoughByTemplateId(bag, entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private void logItemConsume(List<Item.ItemLogBean> logBeans, Player player, EReason reason,
            long logId, String... ext) {
        if (logBeans.isEmpty())
            return;
        if (GameServer.getInstance().isRootDrangServer()
                && (reason == EReason.ITEM_GM || reason == EReason.MAIL_GM)) {
            return;
        }
        String extInfo = "";
        if (ext != null) {
            for (int i = 0; i < ext.length; i++) {
                extInfo += ext[i] + " ";
            }
        }
        for (Item.ItemLogBean logBean : logBeans) {
            try {
                ItemLog bean = LogBeanFactory.createItemConsumLog(player, logBean.getTemplateId(),
                        logBean.getUid(), logBean.getUseNum(), logBean.getTotalNum(),
                        reason.value(), extInfo);
                LogProcessor.getInstance().sendLog(bean);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }

    }

    private void _logItemConsume(int templateId, int num, Player player, EReason reason, long logId,
            String... ext) {
        try {
            String extInfo = "";
            if (ext != null) {
                for (int i = 0; i < ext.length; i++) {
                    extInfo += ext[i] + " ";
                }
            }
            ItemLog bean = LogBeanFactory.createItemConsumLog(player, templateId, templateId, num,
                    player.getBagManager().getItemCount(templateId), reason.value(), extInfo);
            LogProcessor.getInstance().sendLog(bean);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    public Item getItemOrigin(BagManager bag, long id) {
        return _getItemOrigin(bag, id);
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.BACKPACK_SCRIPT.Value();
    }

    @Override
    protected void notifyItemUpdate(Player player, Builder itemChange) {
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        if (itemChange == null) {
            LOGGER.error("notifyItemUpdate error, itemChange is null." + "\n"
                    + ExceptionEx.currentThreadTracesWithOutMineLine());
            return;
        }
        if (itemChange.getItemsCount() == 0 && itemChange.getEquipmentsCount() == 0) {
            return;// 无道具变化
        }
        MessageUtils.send(player, itemChange);
    }
}
