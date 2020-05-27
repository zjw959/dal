package logic.store.req;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg.StoreCommodityBuyInfo;
import org.game.protobuf.s2c.S2CStoreMsg.StoreInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.CommodityCfgBean;
import logic.robot.entity.RobotPlayer;
import logic.store.ReqStoreOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.STORE,
        order = ReqStoreOrder.BUY_GOODS)
public class ReqBuyGoodsEvent extends AbstractEvent {

    public ReqBuyGoodsEvent(RobotThread robot) {
        super(robot, S2CStoreMsg.BuyGoods.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, StoreInfo> storeMap = player.getStoreInfoKV();
        Map<Integer, StoreCommodityBuyInfo> history = player.getBuyHistoryKV();
        // 检测可购买的商品
        List<Integer> canBuyList = new ArrayList<>();
        for (StoreInfo info : storeMap.values()) {
            for (Integer commId : info.getCommoditysList()) {
                CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(commId);
                StoreCommodityBuyInfo his = history.get(commId);
                if (his == null || (his.getTotalBuyCount() + 1 <= commodityCfg.getLimitVal())) {
                    canBuyList.add(commId);
                }
            }
        }
        if (canBuyList.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "商店购买  " + "商店无可购买商品");
            if (robot.requestMultipleEvents.containsKey(FunctionType.STORE)) {
                Log4jManager.getInstance().info(robot.getWindow(),
                        "robot:" + robot.getName() + "商店购买  " + "商店无可购买商品");
                robot.removeCurrentFun();
            }
            super.robotSkipRun();
            return;
        }
        int randomCid = canBuyList.get(RandomUtils.nextInt(canBuyList.size()));
        CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(randomCid);
        // 累计扣除
        int[] priceTypes = commodityCfg.getPriceType();
        int[] priceVals = commodityCfg.getPriceVal();

        Map<Integer, Integer> costMap = new HashMap<>();
        for (int i = 0; i < priceVals.length; i++) {
            int costNum = getInt(priceTypes[i], costMap) + (priceVals[i] * 1);
            costMap.put(priceTypes[i], costNum);
        }
        if (!player.isEnoughItem(costMap)) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "商店购买  " + "资源不足");
            super.robotSkipRun();
            return;
        }

        C2SStoreMsg.BuyGoods.Builder builder = C2SStoreMsg.BuyGoods.newBuilder();
        builder.setCid(randomCid);
        builder.setNum(1);
        SMessage msg = new SMessage(C2SStoreMsg.BuyGoods.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }


    public static int getInt(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0;
        if (obj instanceof Byte) {
            return ((Byte) obj).intValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).intValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        }
        return 0;
    }
}
