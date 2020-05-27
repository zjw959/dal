package logic.buyResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.bag.BagManager;
import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;

import org.game.protobuf.s2c.S2CPlayerMsg.BuyResourcesLog;
import org.game.protobuf.s2c.S2CPlayerMsg.RespBuyResources;
import org.game.protobuf.s2c.S2CPlayerMsg.RespBuyResourcesLog;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import utils.CommonUtil;
import utils.ToolMap;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.ItemRecoverCfgBean;

/**
 * 
 * @Description 购买资源管理器
 * @author LiuJiang
 * @date 2018年8月1日 下午8:39:31
 *
 */
public class BuyResMgr extends PlayerBaseFunctionManager implements IRoleJsonConverter, IAcrossDay {
    /** 购买记录（每日清理） */
    Map<Integer, Integer> buyMap = Maps.newHashMap();

    @Override
    public void tick() {}

    @Override
    public void loginInit() {

    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            RespBuyResourcesLog.Builder resp = RespBuyResourcesLog.newBuilder();
            for (Entry<Integer, Integer> entry : buyMap.entrySet()) {
                BuyResourcesLog.Builder record = BuyResourcesLog.newBuilder();
                record.setCid(entry.getKey());
                record.setCount(entry.getValue());
                record.setCt(ChangeType.DELETE);
                resp.addLogs(record);
            }
            buyMap.clear();
            MessageUtils.send(player, resp);
        }
    }

    /**
     * 请求购买资源
     * 
     * @param cid
     * @param num
     */
    public void reqBuyResources(int cid, int num) {
        ItemRecoverCfgBean cfg = GameDataManager.getItemRecoverCfgBean(cid);
        if (cfg == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "客户端参数异常");
            return;
        }
        int boughtNum = utils.ToolMap.getInt(cid, buyMap);
        ChangeType type = (boughtNum == 0 ? ChangeType.ADD : ChangeType.UPDATE);
        if (boughtNum + num > cfg.getPrice().length) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_ENOUGH_PURCHASES, "购买次数不足");
            return;
        }
        List<Map<String, Object>> priceList = decoder(cfg.getPrice());
        int startIndex = boughtNum;
        int endIndex = boughtNum + num;
        int totalSellCount = 0;
        // 判定购买货币是否足够
        Map<Integer, Integer> totalPrice = new HashMap<Integer, Integer>();
        BagManager bag = player.getBagManager();
        for (int i = startIndex; i < endIndex; i++) {
            Map<String, Object> price = priceList.get(i);
            Map<Integer, Integer> priceScheme = ToolMap.getMap("priceScheme", price);
            int sellCount = ToolMap.getInt("sellCount", price, 0);
            CommonUtil.changeMap(totalPrice, priceScheme);
            totalSellCount += sellCount;
        }
        if (!bag.enoughByTemplateId(totalPrice)) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "道具不足");
            return;
        }
        bag.removeItemsByTemplateIdNoCheck(totalPrice, true, EReason.ITEM_BUY_RESOURCE);
        bag.addItem(cfg.getItemId(), totalSellCount, true, EReason.ITEM_BUY_RESOURCE);

        CommonUtil.changeMap(buyMap, cid, num);

        // 更新购买记录
        RespBuyResourcesLog.Builder resp = RespBuyResourcesLog.newBuilder();
        BuyResourcesLog.Builder record = BuyResourcesLog.newBuilder();
        record.setCid(cid);
        record.setCount(buyMap.get(cid));
        record.setCt(type);
        resp.addLogs(record);
        MessageUtils.send(player, resp);

        RespBuyResources.Builder builder = RespBuyResources.newBuilder();
        builder.setCid(cid);
        builder.setCount(num);
        MessageUtils.send(player, builder);

        // 触发事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.BUY_RESOURCE);
        in.put(EventConditionKey.RESOURCE_CID, cfg.getId());
        player._fireEvent(in, EventType.ITEM_EVENT);
    }

    /**
     * 价格方案解码
     * */
    public static List<Map<String, Object>> decoder(String[] prices) {
        // 格式 500002:50#50-500002:50#50-500002:50#50-500002:50#50-500002:50#50
        List<Map<String, Object>> priceMap = new ArrayList<>(prices.length);
        for (String price : prices) {
            List<String> info = Splitter.on('#').splitToList(price);
            Map<String, String> join =
                    Splitter.on("&").withKeyValueSeparator(":").split(info.get(0));
            Map<Integer, Integer> intMap = Maps.newHashMap();
            join.forEach((k, v) -> intMap.put(Integer.parseInt(k), Integer.parseInt(v)));

            Map<String, Object> map = new HashMap<>();
            map.put("priceScheme", intMap); // 价格方案
            map.put("sellCount", info.get(1)); // 出售数量
            priceMap.add(map);
        }
        return priceMap;
    }

    public Map<Integer, Integer> getBuyMap() {
        return buyMap;
    }
}
