package logic.store.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
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
        order = ReqStoreOrder.REQ_BUY_ITEM_GM)
public class ReqStoreItemGMEvent extends AbstractEvent {

    public ReqStoreItemGMEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
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
                    "robot:" + robot.getName() + "商店GM  " + "商店无可购买商品,不增加道具");
            super.robotSkipRun();
            return;
        }

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        str.append("500001").append(":").append("10000").append(",");
        str.append("570002").append(":").append("1000").append(",");
        str.append("570001").append(":").append("1000").append(",");
        str.append("570006").append(":").append("1000").append(",");
        str.append("500018").append(":").append("1000").append(",");
        str.append("570005").append(":").append("1000");

        String gmContent = str.toString();
        int length = StringUtils.split(gmContent, " ").length;
        if (length > 1) {
            C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
            builder.setChannel(1);
            builder.setFun(1);
            builder.setContent(str.toString());
            SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), this.resOrder);
            sendMsg(msg);
        }

    }

}
