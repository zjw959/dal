package logic.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;

import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.ItemTimeCfgBean;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EItemType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.item.bean.Item;
import logic.support.LogicScriptsUtils;
import server.GameServer;
import utils.ExceptionEx;
import utils.IdGenerator;

public class ItemUtils {

    private static final Logger LOGGER = Logger.getLogger(ItemUtils.class);

    /**
     * 根据给的Id 和数量创建 道具列表
     * 
     * @param templateId
     * @param amount 可以为0（有时构建消息时需要）
     * @return
     */
    public static List<Item> createItems(int templateId, int amount) {
        BaseGoods _itemBean = GameDataManager.getBaseGoods(templateId);
        if (_itemBean == null) {
            String traces = "";
            if (GameServer.getInstance().isTestServer()) {
                traces = ExceptionEx.currentThreadTraces();
            }
            LOGGER.error(ConstDefine.LOG_ERROR_CONFIG_PREFIX
                    + "createItems By id & num . can not found config data. templateId:"
                    + templateId + traces);
            return Collections.EMPTY_LIST;
        }
        List<Item> itemList = new ArrayList<>();
        // if (maxNum < 1) {
        // LOGGER.error(
        // "createItems By id & num . maxNum < 1 ,exit create to avoid dead loop !!!.");
        // return Collections.EMPTY_LIST;
        // }
        int num = amount;
        // 限时道具
        long deadTime = 0;
        ItemTimeCfgBean _timeBean = GameDataManager.getItemTimeCfgBean(templateId);
        if (_timeBean != null) {
            switch (_timeBean.getLimitType()) {
                case 1:// 从获得之时算起
                    deadTime = System.currentTimeMillis() + _timeBean.getLimitData() * 60 * 1000;
                    break;
                case 2:// 固定时间到期
                    deadTime = _timeBean.getLimitData() * 1000;
                    break;
                default:
                    break;
            }
        }
        // 是否可堆叠
        boolean isStackable = isStackable(_itemBean);
        EItemType type = EItemType.itemType(_itemBean.getSuperType(), templateId);
        while (num >= 0) {
            int _num = num;
            if (_num > 0) {
                if (!isStackable) {// 不可叠加 则按照一个实例一个数量
                    _num = 1;
                }
            }
            num -= _num;
            try {
                Item item = type.getClazz().newInstance();
                if (_itemBean.getSuperType() == EItemType.TOKEN.getValue()) {// 代币类道具绑定唯一id，因为客户端获取代币时还是从背包获取，兼容以前背包结构
                    item.setId(templateId);// 代币的模板id具有唯一性
                } else {
                    if (_num <= 0) {// 非代币类道具数量不能为0,代币类道具是因为构建协议需要生成数量为0的道具消息模板
                        break;
                    }
                    item.setId(IdGenerator.getItemId(_itemBean.getSuperType()));
                }
                item.setTemplateId(templateId);
                item.setNum(_num);
                item.setDeadTime(deadTime);
                // 初始化额外数据
                item.initialize();
                itemList.add(item);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("create Items By templateId. failed.  init ItemObject Error : "
                        + ExceptionEx.e2s(e));
                return Collections.EMPTY_LIST;
            }
            if (num <= 0) {
                break;
            }
        }
        return itemList;
    }

    private static int _scriptId(int templateId) {
        BaseGoods _itemBean = GameDataManager.getBaseGoods(templateId);
        if (_itemBean == null) {
            LOGGER.error(ConstDefine.LOG_ERROR_CONFIG_PREFIX
                    + "get item script. can not find itemBean config. " + templateId);
            return 0;
        }
        return EItemType.scriptId(_itemBean.getSuperType());
    }

    /* ———————————————————————————脚本方法——————————————————————————————————— */

    public static void putTrigger(Player player, int templateId, boolean isExist) {
        int scriptId = _scriptId(templateId);
        if (scriptId == 0) {
            return;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        script.putTrigger(player, templateId, isExist);
        // 物品入包触发脚本
        IItemScript putTriggerAll =
                LogicScriptsUtils.getIItemScript(EScriptIdDefine.PUT_TRIGGER.Value());
        putTriggerAll.putTrigger(player, templateId, isExist);
    }

    public static boolean useLimit(Player player, int templateId) {
        int scriptId = _scriptId(templateId);
        if (scriptId == 0) {
            return false;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        return script.usePremise(player, templateId);
    }

    public static boolean doUsebyTemplateId(Player player, int num, int templateId,
            List<Integer> customParam, Map<Integer, Integer> rewardItems, boolean isNotify,
            ItemList.Builder itemMsg) {
        int scriptId = _scriptId(templateId);
        if (scriptId == 0) {
            return false;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        return script.doUsebyTemplateId(player, num, templateId, customParam, rewardItems, isNotify,
                itemMsg);
    }

    public static boolean doUsebyUid(Player player, int num, long uid, List<Integer> customParam,
            Map<Integer, Integer> rewardItems, boolean isNotify, ItemList.Builder itemMsg) {

        Item item = player.getBagManager().getItemCopy(uid);
        if (item == null) {
            return false;
        }
        int scriptId = _scriptId(item.getTemplateId());
        if (scriptId == 0) {
            return false;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        return script.doUsebyUid(player, num, uid, customParam, rewardItems, isNotify, itemMsg);
    }

    public static boolean autoUseNumEnough(Player player, int num, int templateId) {
        int scriptId = _scriptId(templateId);
        if (scriptId == 0) {
            return false;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        return script.autoUseNumEnough(player, num, templateId);
    }

    public static boolean isAutoUse(int templateId) {
        int scriptId = _scriptId(templateId);
        if (scriptId == 0) {
            return false;
        }
        IItemScript script = LogicScriptsUtils.getIItemScript(scriptId);
        if (script == null) {
            LOGGER.error("script 脚本为空. scriptId:" + scriptId + ",templateId:" + templateId);
            return false;
        }
        return script.isAutoUse(templateId);
    }

    /* —————————————————————————配置表属性——————————————————————————— */

    /**
     * 道具是否可使用
     * 
     * @return
     */
    public static boolean canUse(int templateId) {
        BaseGoods _item = GameDataManager.getBaseGoods(templateId);
        if (_item != null) {
            return _item.getSuperType() == EItemType.PACKAGE.getValue();// 目前只有礼包类道具可以在背包直接使用
        }
        LOGGER.error("item bean can not find  in config data. templateId: " + templateId);
        return false;
    }


    /**
     * 是否可以批量使用
     * 
     * @return
     */
    public static boolean canBatchUse(int templateId) {
        BaseGoods _item = GameDataManager.getBaseGoods(templateId);
        if (_item != null) {
            return _item.getSuperType() == EItemType.PACKAGE.getValue();// 目前只有礼包类道具可以在背包直接使用
        }
        LOGGER.error("item bean can not find  in config data. templateId: " + templateId);
        return false;
    }

    public static boolean recoredNew(int templateId) {
        // ItemData _item = GameDataManager.getItemData(templateId);
        // if (_item != null) {
        // return _item.getShowNew() == 1;
        // }
        // LOGGER.error("item bean can not find in config data. templateId: " + templateId);
        return false;
    }

    /**
     * 是否可回收
     * 
     * @return
     */
    public static boolean canRecycle(int templateId) {
        // ItemData _item = GameDataManager.getItemData(templateId);
        // if (_item != null) {
        // return _item.getRecycle() == 1;
        // }
        // LOGGER.error("item bean can not find in config data. templateId: " + templateId);
        return false;
    }

    /**
     * 可否合成
     * 
     * @return
     */
    public static boolean canCompound(int templateId) {
        // ItemData _item = GameDataManager.getItemData(templateId);
        // if (_item != null) {
        // return _item.getCompound() == 1;
        // }
        // LOGGER.error("item bean can not find in config data. templateId: " + templateId);
        return false;
    }

    /**
     * 道具是否数量叠加
     * 
     * @param templateId
     * @return
     */
    public static boolean isStackable(int templateId) {
        return getMaxNum(templateId) != 1;
    }

    /**
     * 道具是否数量叠加
     * 
     * @param templateId
     * @return
     */
    public static boolean isStackable(BaseGoods goods) {
        return goods.getGridMax() != 1;
    }

    public static int getMaxNum(int templateId) {
        BaseGoods _item = GameDataManager.getBaseGoods(templateId);
        if (_item != null) {
            return _item.getGridMax();
        }
        LOGGER.error("item bean can not find  in config data. templateId: " + templateId);
        return 0;
    }

    public static int getItemType(int templateId) {
        BaseGoods _item = GameDataManager.getBaseGoods(templateId);
        if (_item != null) {
            return _item.getSubType();
        }
        LOGGER.error("item bean can not find  in config data. templateId: " + templateId);
        return 0;
    }

    /**
     * 解包并发放道具到背包
     */
    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> unpackItems(Player player, List<Integer> customParame,
            Map<Integer, Integer> drops, boolean isNotify, EReason reason) {
        if (drops == null || drops.size() == 0) {
            return MapUtil.newHashMap();
        }
        Map<Integer, Integer> out = Maps.newLinkedHashMap();
        for (Entry<Integer, Integer> item : drops.entrySet()) {
            int id = item.getKey();
            int num = item.getValue();
            if (id == 0 || num == 0)
                continue;
            BaseGoods goods = GameDataManager.getBaseGoods(id);
            for (int i = 0; i < num; i++) {
                ItemPackageHelper.unpack(goods.getUseProfit(), customParame, out);
            }
        }
        // 添加道具
        player.getBagManager().addItems(out, isNotify, reason);
        return out;
    }

    public static void mergeItemMap(Map<Integer, Integer> map1, Map<Integer, Integer> map2) {
        if (map1 == null || map2 == null) {
            return;
        }
        for (Map.Entry<Integer, Integer> temp : map2.entrySet()) {
            int key = temp.getKey();
            int value = temp.getValue();
            if (map1.containsKey(key)) {
                int num = map1.get(key) + value;
                map1.put(key, num);
            } else {
                map1.put(key, value);
            }
        }
    }

    public static void autoMap(Map<Integer, Integer> map, int number) {
        if (map == null || number <= 1) {
            return;
        }
        for (Map.Entry<Integer, Integer> temp : map.entrySet()) {
            int value = temp.getValue();
            map.put(temp.getKey(), value * number);
        }
    }

    /**
     * 按比例增长数值
     */
    public static void autoMapByRadio(Map<Integer, Integer> map, double radio) {
        if (map == null || radio <= 0) {
            return;
        }
        for (Map.Entry<Integer, Integer> temp : map.entrySet()) {
            int value = (int) Math.ceil(temp.getValue() * (1.0 + radio));
            map.put(temp.getKey(), value);
        }
    }
    /* —————————————————————————配置表属性 END——————————————————————————— */

    // public static Map<Long, Integer> combineIds(List<ItemIdNumWrapper> wrappers) {
    // Map<Long, Integer> combine = new HashMap<>();
    // for (int i = 0; i < wrappers.size(); i++) {
    // if (combine.containsKey(wrappers.get(i).getId())) {
    // continue;
    // }
    // int count = wrappers.get(i).getNum();
    // for (int k = i + 1; k < wrappers.size(); k++) {
    // if (wrappers.get(i).getId() == wrappers.get(k).getId()) {
    // count += wrappers.get(k).getNum();
    // }
    // }
    // combine.put(wrappers.get(i).getId(), count);
    // }
    // return combine;
    // }
    //
    // public static Map<Integer, Integer> combineTemplateIds(List<ItemTemplateIdNumWrapper>
    // wrappers) {
    // Map<Integer, Integer> combine = new HashMap<>();
    // for (int i = 0; i < wrappers.size(); i++) {
    // if (combine.containsKey(wrappers.get(i).getTemplateId())) {
    // continue;
    // }
    // int count = wrappers.get(i).getNum();
    // for (int k = i + 1; k < wrappers.size(); k++) {
    // if (wrappers.get(i).getTemplateId() == wrappers.get(k).getTemplateId()) {
    // count += wrappers.get(k).getNum();
    // }
    // }
    // combine.put(wrappers.get(i).getTemplateId(), count);
    // }
    // return combine;
    // }


    public static void _main() {
        List<Item> testBags = new ArrayList<>();
        List<Item> items = createItems(50001, 250);
        List<Item> _items = createItems(70001, 5);
        testBags.addAll(items);
        testBags.addAll(_items);
        // JSONArray array = new JSONArray();
        // for (Item item : testBags) {
        // LOGGER.info(item.toString());
        // JSONObject object = new JSONObject();
        // object.put("item", item.toJson());
        // array.add(object);
        // }
        // testBags.clear();
        // JSONObject _a = new JSONObject();
        // _a.put("te", array);
        // LOGGER.info(_a.toString());
        // JSONObject _o = JSON.parseObject(_a.toString());
        // JSONArray _arr = _o.getJSONArray("te");
        // for (Object __a : _arr) {
        // testBags.add(createItem(((JSONObject) __a).getJSONObject("item")));
        // }
        //
        // for (Item item : testBags) {
        // LOGGER.info(item.toString());
        // }

    }
}
