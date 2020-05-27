package logic.bag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.ILoginInit;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IView;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.DiscreteDataID;
import logic.constant.EEventType;
import logic.constant.EItemType;
import logic.constant.EReason;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.item.IItemScript;
import logic.item.ItemUtils;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.item.bean.SkinItem;
import logic.msgBuilder.ItemMsgBuilder;
import logic.support.LogicScriptsUtils;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import server.GameServer;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.MapEx;
import utils.ToolMap;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.DiscreteDataCfgBean;
import data.bean.EquipmentCfgBean;

/**
 * 角色背包管理类
 */
public class BagManager extends PlayerBaseFunctionManager implements IRoleJsonConverter,
        ICreateRoleInitialize, ICreatePlayerInitialize, ILoginInit, IView {
    private static transient final Logger LOGGER = Logger.getLogger(BagManager.class);

    /** 道具模板对应道具唯一ID 索引 */
    private transient Map<Integer, Set<Long>> templateIdRegistry = new HashMap<>();
    private transient Map<Integer, Set<Long>> bagTypeIdRegistry = new HashMap<>();
    /** 新道具ID集合 */
    private transient Set<Long> newItemIds = new HashSet<>();

    private Map<Long, Item> itemKV = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void createRoleInitialize() {
        DiscreteDataCfgBean discreteDataCfgBean =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.INIT_PLAYER);
        Map dataMap = discreteDataCfgBean.getData();
        Map<Integer, Integer> goods = (Map<Integer, Integer>) dataMap.get("goods");
        addItems(goods, false, EReason.CREATE);
    }

    @Override
    public void createPlayerInitialize() {
        Collection<Item> collection = itemKV.values();
        for (Item _item : collection) {
            _setTemplateIds(_item);
            _setBagTypeIds(_item);
        }
    }

    @Override
    public void loginInit() {
        // Message.ItemMsg.ResItems.Builder builder = Message.ItemMsg.ResItems.newBuilder();
        // builder.addAllNewIds(this.newItemIds);
        // builder.addAllItems(this._buildBackpackItemInfo());
        // MessageUtils.send(player, builder);
    }

    @Override
    public IView toView() {
        // 根据需要进行返回. 目前只返回当前有精灵使用的装备
        Map<Long, Item> _itemKV = new HashMap<>();
        for (Item item : itemKV.values()) {
            if (item instanceof EquipItem) {
                EquipItem _item = (EquipItem) item;
                if (_item.getHeroId() > 0) {
                    _itemKV.put(item.getId(), item);
                }
            } else if (item instanceof SkinItem) {
                SkinItem _item = (SkinItem) item;
                if (_item.getHeroId() > 0) {
                    _itemKV.put(item.getId(), item);
                }
            }
        }
        this.itemKV = _itemKV;
        return this;
    }

    @Override
    public JsonElement toViewJson(String fullJsonData) {
        BagManager baseFunMan = (BagManager) GsonUtils.fromJson(fullJsonData, getClass());
        JsonElement _json = GsonUtils.toJsonTree(baseFunMan.toView());
        return _json;
    }

    public void reqClearNewFlag(List<Long> ids) {
        if (!ids.isEmpty()) {
            for (long id : ids) {
                if (this.newItemIds.contains(id)) {
                    this.newItemIds.remove(id);
                }
            }
        }
    }

    /**
     * 获取背包最大容量
     * 
     * @param bagType
     * @return
     */
    public int getMaxCapacity(int bagType) {
        DiscreteDataCfgBean cfg = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.ROLE_BAG);
        int capacity = 0;
        Integer cap = (Integer) cfg.getData().get(bagType);
        if (cap != null) {
            capacity = cap.intValue();
        }
        // 不存在的背包始终有空间
        if (capacity == 0) {
            capacity = Integer.MAX_VALUE;
        }
        return capacity;
    }

    /**
     * 取得空闲容量
     * 
     * @return
     */
    public int getFreeCapacity(int bagType) {
        Set<Long> values = bagTypeIdRegistry.get(bagType);
        int max = getMaxCapacity(bagType);
        int now = values != null ? values.size() : 0;
        return Math.max(0, max - now);
    }

    /** 获取所有背包道具 */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>(itemKV.values());
        return items;
    }

    /** 获取所有背包道具 */
    public List<Item> getAllItemsByItemsKVNoCopy() {
        List<Item> items = new ArrayList<>();
        Collection<Item> itemIds = itemKV.values();
        for (Item item : itemIds) {
            items.add(item);
        }
        return items;
    }

    /** 获取指定道具数量 */
    public int getItemCount(int templateId) {
        int count = 0;
        Set<Long> ids = templateIdRegistry.get(templateId);
        if (ids != null) {
            for (Long id : ids) {
                Item _item = itemKV.get(id);
                if (_item == null) {
                    LOGGER.error("getItemCount error, itemKV not exists!. id:" + id
                            + ExceptionEx.currentThreadTraces());
                    continue;
                }
                count += _item.getNum();
            }
        } else {
            BaseGoods base = GameDataManager.getBaseGoods(templateId);
            if (base == null) {
                LOGGER.error("getItemCount error, templateId not exists!. id:" + templateId
                        + ExceptionEx.currentThreadTraces());
                return 0;
            }
            if (base.getSuperType() == EItemType.TOKEN.getValue()) {
                // 代币类道具特殊处理
                int scriptId = EItemType.TOKEN.getScriptId();
                IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
                return script.getItemCount(player, templateId);
            }
        }
        return count;
    }

    public List<Item> getItemsByBagType(int bagType) {
        List<Item> items = new ArrayList<>();
        Set<Long> itemIds = bagTypeIdRegistry.get(bagType);
        if (itemIds != null) {
            for (Long _itemId : itemIds) {
                Item _item = itemKV.get(_itemId);
                if (_item == null) {
                    LOGGER.error("type error, bagTypeIdRegistry get. itemKV not exists!. id:"
                            + _itemId + ",type:" + bagType);
                    continue;
                }
                items.add(_item.copy());
            }
        }
        return items;
    }

    /**
     * 判断所给的模板和数量的道具是否足够
     * 
     * @param templateId 道具模板ID
     * @param num 道具数量
     * @return 返回是否道具足够
     */
    public boolean enoughByTemplateId(int templateId, int num) {
        if (num < 0) {
            LOGGER.error("remove item  ,parameter error . must  be  a positive number");
            return false;
        }
        if (num == 0) {
            return true;
        }
        if (ItemUtils.isAutoUse(templateId)) {
            if (!ItemUtils.autoUseNumEnough(player, num, templateId)) {
                return false;
            }
            return true;
        } else {
            Set<Long> ids = templateIdRegistry.get(templateId);
            if (ids == null || ids.isEmpty()) {
                return false;
            }
            for (Iterator<Long> ite = ids.iterator();;) {
                if (!ite.hasNext()) {
                    break;
                }
                long itemId = ite.next();
                Item _item = itemKV.get(itemId);
                if (_item == null) {
                    LOGGER.error("ERROR : enoughByTemplateId . tempId:" + templateId + " itemId:"
                            + itemId + ExceptionEx.currentThreadTraces());
                }
                if (_item.getNum() >= num) {
                    num = 0;
                    break;
                }
                num -= _item.getNum();
            }
            return num == 0;
        }
    }

    /**
     * 判断所给的模板和数量的道具是否足够
     * 
     * @param map 道具模板ID,num
     * @return 返回是否道具足够
     */
    public boolean enoughByTemplateId(Map<Integer, Integer> map) {
        Iterator<Entry<Integer, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, Integer> entry = it.next();
            if (!enoughByTemplateId(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加指定数量的道具 如果道具创建失败.返回空集合
     * 
     * @param templateId
     * @param num
     * @return 如果道具失败.返回空集合.需要判断返回结果. 创建成功.返回成功创建的道具为副本道具，可根据副本道具的唯一id获取背包真实源道具
     */
    public List<Item> addItem(int templateId, int num, boolean isNotify, EReason reason,
            String... ext) {
        return addItem(templateId, num, isNotify, reason, true, ext);
    }

    /**
     * 添加指定数量的道具 如果道具创建失败.返回空集合
     * 
     * @param templateId
     * @param num
     * @return 如果道具失败.返回空集合.需要判断返回结果. 创建成功.返回成功创建的道具为副本道具，可根据副本道具的唯一id获取背包真实源道具
     */
    public List<Item> addItem(int templateId, int num, boolean isNotify, EReason reason,
            boolean needMail, String... ext) {
        if (num <= 0) {
            String stack = "";
            if (GameServer.getInstance().isTestServer()) {
                stack = ExceptionEx.currentThreadTraces();
            }
            LOGGER.error("add item . item number is 0." + stack);
            return Collections.EMPTY_LIST;
        }
        return LogicScriptsUtils.getIBackpackScript().addItem(this, this.player, templateId, num,
                isNotify, reason, needMail, ext);
    }

    /**
     * 根据列表创建道具。如果要创建的道具可以叠加。则会在原道具上叠加数量
     * 
     * @param templateIdNums
     * @param isNotify
     * @param reason
     * @return 创建成功.返回成功创建的道具为副本道具，可根据副本道具的唯一id获取背包真实源道具
     */
    public List<Item> addItems(Map<Integer, Integer> templateIdNums, boolean isNotify,
            EReason reason, String... ext) {
        return addItems(templateIdNums, isNotify, reason, true, ext);
    }

    /**
     * 根据列表创建道具。如果要创建的道具可以叠加。则会在原道具上叠加数量
     * 
     * @param templateIdNums
     * @param isNotify
     * @param reason
     * @return 创建成功.返回成功创建的道具为副本道具，可根据副本道具的唯一id获取背包真实源道具
     */
    @SuppressWarnings("unchecked")
    public List<Item> addItems(Map<Integer, Integer> templateIdNums, boolean isNotify,
            EReason reason, boolean needMail, String... ext) {
        if (!_checkNumber(templateIdNums, "AddItems")) {
            return Collections.EMPTY_LIST;
        }
        return LogicScriptsUtils.getIBackpackScript().addItem(this, this.player, templateIdNums,
                isNotify, reason, needMail, ext);
    }


    /**
     * 根据道具的唯一ID和数量列表删除道具
     * 
     * @param idNums
     * @return
     */
    public boolean removeItemsByIds(Map<Long, Integer> idNums, boolean isNotify, EReason reason) {
        if (!_checkNumber(idNums, "RemoveItemsById")) {
            return false;
        }
        return LogicScriptsUtils.getIBackpackScript().removeItemsById(this, this.player, idNums,
                isNotify, reason);
    }

    /**
     * 严禁调用
     * @param reason
     */
    @Deprecated
    public void removeUnUsedItems(EReason reason) {
        if (GameServer.getInstance().isTestServer()) {
            LogicScriptsUtils.getIBackpackScript().removeUnUsdItem(this, player,
                    true, reason);
        } else {
            LOGGER.error(
                    "正式服调用 removeUnUsedItems!!!!!!!!!!!!!" + ExceptionEx.currentThreadTraces());
        }
    }

    /**
     * 根据道具的模板ID 删除道具
     *
     * @param templateId
     * @param num
     * @return
     */
    public boolean removeItemByTempId(int templateId, int num, boolean isNotify, EReason reason) {
        if (num <= 0) {
            LOGGER.error("remove item, parameter error . must be a positive number. templateId:"
                    + templateId + ",num:" + num);
            return false;
        }
        return LogicScriptsUtils.getIBackpackScript().removeItemByTemplateId(this, player,
                templateId, num, isNotify, reason);
    }

    /**
     * 根据道具的模板ID和数量列表删除. 一个失败都将失败
     * 
     * 默认会检查道具是否足够
     * 
     * @param templateIdNums
     * @return
     */
    public boolean removeItemsByTemplateIdWithCheck(Map<Integer, Integer> templateIdNums,
            boolean isNotify, EReason reason) {
        return _removeItemsByTemplateId(templateIdNums, isNotify, reason, true);
    }

    /**
     * 根据道具的模板ID和数量列表删除. 一个失败都将失败
     * 
     * 不会检查道具是否足够
     * 
     * @param templateIdNums
     * @return
     */
    public boolean removeItemsByTemplateIdNoCheck(Map<Integer, Integer> templateIdNums,
            boolean isNotify, EReason reason) {
        return _removeItemsByTemplateId(templateIdNums, isNotify, reason, false);
    }


    /**
     * 获取道具副本
     * 
     * @param itemId
     * @return
     */
    public Item getItemCopy(long itemId, String reason) {
        Item item = itemKV.get(itemId);
        if (item == null) {
            LOGGER.error("can not find item, itemId:" + itemId + " playerId:" + player.getPlayerId()
                    + " reason:" + reason
                    + "\n" + ExceptionEx.currentThreadTracesWithOutMineLine());
            return null;
        }
        return item.copy();
    }

    /**
     * 获取道具副本
     * 
     * @param itemId
     * @return
     */
    public Item getItemCopy(long itemId) {
        return getItemCopy(itemId, "");
    }

    /**
     * 根据模板id获取items副本
     * 
     * @param templateId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Item> getItemCopyByTemplateId(int templateId) {
        Set<Long> ids = templateIdRegistry.get(templateId);
        if (ids == null || ids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Item> items = new ArrayList<>();
        for (Iterator<Long> iterator = ids.iterator();;) {
            if (!iterator.hasNext()) {
                break;
            }
            long itemId = iterator.next();
            Item item = itemKV.get(itemId);
            if (item == null) {
                LOGGER.error(
                        "find item id in templateIdRegistry.but did not in backpack . remove from registry. itemId:"
                                + itemId + ExceptionEx.currentThreadTraces());
                iterator.remove();
            } else {
                items.add(item.copy());
            }
        }
        return items;
    }

    protected void addNewItemId(long itemId) {
        newItemIds.add(itemId);
    }

    protected boolean isExist(int tempId) {
        return templateIdRegistry.containsKey(tempId);
    }

    /**
     * 不做任何道具实例合并，只添加最后结果
     * 
     * @param item 返回添加的道具 如果是自使用道具. 则返回null
     */
    protected Item putItem(Item item) {
        _putItemNoCopy(item.copy());
        // 触发获得事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.ADD_ITEM);
        in.put(EventConditionKey.ITEM_CID, item.getTemplateId());
        in.put(EventConditionKey.COUNT, item.getNum());
        
        BaseGoods baseGoods = GameDataManager.getBaseGoods(item.getTemplateId());
        if(baseGoods instanceof EquipmentCfgBean){
            EquipmentCfgBean eqBean = (EquipmentCfgBean)baseGoods;
            in.put(EventConditionKey.QUALITY, eqBean.getQuality());
            in.put(EventConditionKey.SUPER_TYPE, eqBean.getSuperType());
        }
        
        player._fireEvent(in, EEventType.ITEM_EVENT.value());
        return item;
    }

    /**
     * 根据道具唯一ID 和 给定数量移除道具
     * 
     * 只能移除一个Item 实例.数量不足将移除失败
     * 
     * @param id 道具唯一ID
     * @param num 需要移除的数量
     * @param used 使用道具
     * @return 是否移除成功
     */
    protected boolean removeById(long id, int num, ItemList.Builder itemMsg,
            List<Item.ItemLogBean> used) {
        // 数量为0 背包中可能不存在实例 直接返回 true
        if (num <= 0) {
            LOGGER.error("Item Remove Error. num <= 0 . id:" + id + ",num:" + num
                    + ExceptionEx.currentThreadTraces());
            return true;
        }

        Item _item = itemKV.get(id);
        if (_item == null || _item.getNum() < num) {
            LOGGER.error("Item Remove Error. _item.getNum() < num . id:" + id + ",num:" + num
                    + ",itemNum:" + _item.getNum()
                    + ExceptionEx.currentThreadTraces());
            return false;
        }
        _item.setNum(_item.getNum() - num);

        if (_item.getNum() == 0) {
            itemKV.remove(id);
            ItemMsgBuilder.addItemMsg(itemMsg, ChangeType.DELETE, _item);
            Set<Long> ids = templateIdRegistry.get(_item.getTemplateId());
            if (ids == null || !ids.contains(id)) {
                LOGGER.error(
                        "Item Remove Error. item exits in backpack  but do not in templateIdRegistry. tempId:"
                                + _item.getTemplateId() + " id:"
                                + id + ExceptionEx.currentThreadTraces());
            } else {
                ids.remove(id);
            }
            Set<Long> typeIds =
                    bagTypeIdRegistry.get(GameDataManager.getBaseGoods(_item.getTemplateId())
                            .getBagType());
            if (typeIds == null || !typeIds.contains(id)) {
                LOGGER.error(
                        "Item Remove Error. item exits in backpack but do not in bagTypeIdRegistry."
                                + ExceptionEx.currentThreadTraces());
                return true;
            } else {
                typeIds.remove(id);
            }
        } else {
            ItemMsgBuilder.addItemMsg(itemMsg, ChangeType.UPDATE, _item);
        }
        Item surplusItem = _item.copy();
        used.add(surplusItem.toLogBean(0, num, getItemCount(_item.getTemplateId()), false));
        if (newItemIds.contains(id)) {
            newItemIds.remove(id);
        }
        return true;
    }

    /**
     * 根据ID 和数量判断给定ID的道具实例数量是否足够
     * 
     * @param id
     * @param num
     * @return
     */
    protected boolean enoughById(long id, int num) {
        if (num < 0) {
            LOGGER.error("remove item  ,parameter error . must  be  a positive number");
            return false;
        }
        if (num == 0) {
            return true;
        }
        Item _item = itemKV.get(id);
        if (_item == null || _item.getNum() < num) {
            return false;
        }
        return true;
    }

    /**
     * 根据ID 和数量判断给定ID的道具实例数量是否足够
     * 
     * @param map
     * @return
     */
    protected boolean enoughById(Map<Long, Integer> map) {
        Iterator<Entry<Long, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, Integer> entry = it.next();
            if (!enoughById(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据道具配置表ID 和给定数量移除背包中的道具.
     * 
     * @param templateId 道具配置表ID
     * @param num 要移除的数量
     * @param used 使用的道具
     * @return 移除结果 true (if item is exits and num enoughByTemplateId)
     */
    protected boolean removeByTemplateId(int templateId, int num, ItemList.Builder builder,
            List<Item.ItemLogBean> used) {
        if (num < 0) {
            LOGGER.error("remove item  ,parameter error . must  be  a positive number");
            return false;
        }
        if (num == 0) {
            return true;
        }
        Set<Long> ids = templateIdRegistry.get(templateId);
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int count = num;
        Map<Long, Integer> needRemove = new HashMap<>();
        if (!_enoughByTemplateId(templateId, count, needRemove)) {
            return false;
        }
        return _removeByIdMap(needRemove, builder, used);
    }

    /**
     * 此方法直接移除所给集合 不做任何检查
     * 
     * @param needRemove
     * @param surplus 剩余道具
     * @param used 使用的道具
     * @return
     */
    private boolean _removeByIdMap(Map<Long, Integer> needRemove, ItemList.Builder builder,
            List<Item.ItemLogBean> used) {
        if (needRemove == null) {
            return false;
        }
        if (needRemove.isEmpty()) {
            return true;
        }
        for (Map.Entry<Long, Integer> entry : needRemove.entrySet()) {
            removeById(entry.getKey(), entry.getValue(), builder, used);
        }
        return true;
    }

    private void _putItemNoCopy(Item item) {
        // 无论是新加还是修改的以前的数据，都直接替换
        itemKV.put(item.getId(), item);
        _setTemplateIds(item);
        _setBagTypeIds(item);
    }

    private void _setTemplateIds(Item item) {
        Set<Long> _oldIds = templateIdRegistry.get(item.getTemplateId());
        if (_oldIds != null) {
            _oldIds.add(item.getId());
        } else {
            Set<Long> ids = new HashSet<>();
            ids.add(item.getId());
            templateIdRegistry.put(item.getTemplateId(), ids);
        }
    }

    private void _setBagTypeIds(Item item) {
        int type = GameDataManager.getBaseGoods(item.getTemplateId()).getBagType();
        Set<Long> _oldIds = bagTypeIdRegistry.get(type);
        if (_oldIds != null) {
            _oldIds.add(item.getId());
        } else {
            Set<Long> ids = new HashSet<>();
            ids.add(item.getId());
            bagTypeIdRegistry.put(type, ids);
        }
    }

    /**
     * 判断所给的模板和数量的道具是否足够
     * 
     * @param templateId 道具模板ID
     * @param num 道具数量
     * @param needRemove 如果数量足够. 返回道具唯一ID 和需要扣除的数量
     * @return 返回是否道具足够
     */
    private boolean _enoughByTemplateId(int templateId, int num, Map<Long, Integer> needRemove) {
        if (num < 0) {
            LOGGER.error("remove item, parameter error. must be a positive number");
            return false;
        }
        if (num == 0) {
            return true;
        }
        Set<Long> ids = templateIdRegistry.get(templateId);
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (Iterator<Long> ite = ids.iterator();;) {
            if (!ite.hasNext()) {
                break;
            }
            Item _item = itemKV.get(ite.next());
            if (_item.getNum() >= num) {
                needRemove.put(_item.getId(), num);
                num = 0;
                break;
            }
            needRemove.put(_item.getId(), _item.getNum());
            num -= _item.getNum();
        }
        return num == 0;
    }

    private boolean _removeItemsByTemplateId(Map<Integer, Integer> templateIdNums,
            boolean isNotify, EReason reason, boolean isCheck) {
        if (!_checkNumber(templateIdNums, "removeItemsByTemplateId")) {
            return false;
        }
        return LogicScriptsUtils.getIBackpackScript().removeItemsByTemplateId(this, this.player,
                templateIdNums, isNotify, reason, isCheck);
    }

    private <T> boolean _checkNumber(Map<T, Integer> idNumbers, String operate) {
        if (idNumbers == null || idNumbers.isEmpty()) {
            LOGGER.error(operate + " items . item  is empty." + ExceptionEx.currentThreadTraces());
            return false;
        }
        for (Map.Entry<T, Integer> idNums : idNumbers.entrySet()) {
            if (idNums.getValue() < 0) {
                LOGGER.error(operate + " items .must  be  a positive number ; id :"
                        + idNums.getKey());
                return false;
            }
        }
        return true;
    }

    /**
     * 获取真实道具源 慎用！
     * 
     * @param itemId
     * @return
     */
    public Item getItemOrigin(long itemId) {
        Item item = itemKV.get(itemId);
        if (item == null) {
            LOGGER.warn("can not find item, itemId:" + itemId + "\n"
                    + ExceptionEx.currentThreadTracesWithOutMineLine());
            return null;
        }
        return item;
    }

    /**
     * 取得玩家最大装备等级
     */
    public int getMaxEquipLvl(int equipId) {
        int maxLvl = 0;
        for (Item item : itemKV.values()) {
            if (!(item instanceof EquipItem)) {
                continue;
            }
            EquipItem equip = (EquipItem) item;
            if (equipId != 0 && equipId != item.getTemplateId()) {
                continue;
            }
            maxLvl = Math.max(maxLvl, equip.getLevel());
        }
        return maxLvl;
    }

    /**
     * 检查背包空间是否足够
     * 
     * @param player
     * @param goods
     * @return
     */
    public boolean checkBagSpace(Map<Integer, Integer> goods) {
        if (MapEx.isEmpty(goods))
            return true;
        // 需要背包容量
        Map<Integer, Integer> needBackpackCapacity = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : goods.entrySet()) {
            int tempId = e.getKey();
            int num = e.getValue();
            BaseGoods baseGoods = GameDataManager.getBaseGoods(tempId);
            if (ItemUtils.isStackable(tempId)) {
                int count = getItemCount(tempId);
                if (count == 0) {
                    int needCapacity =
                            ToolMap.getInt(baseGoods.getBagType(), needBackpackCapacity) + 1;
                    needBackpackCapacity.put(baseGoods.getBagType(), needCapacity);
                } else {
                    int totalMax = baseGoods.getTotalMax();
                    if (count + num > totalMax) {
                        return false;
                    }
                }
            } else {
                int needCapacity =
                        ToolMap.getInt(baseGoods.getBagType(), needBackpackCapacity) + num;
                needBackpackCapacity.put(baseGoods.getBagType(), needCapacity);
            }
        }
        // 检查需要的容量
        for (Entry<Integer, Integer> entry : needBackpackCapacity.entrySet()) {
            if (getFreeCapacity(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }


}
