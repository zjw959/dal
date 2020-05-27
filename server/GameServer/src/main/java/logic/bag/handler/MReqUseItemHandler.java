package logic.bag.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.GameErrorCode;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SItemMsg;
import org.game.protobuf.c2s.C2SItemMsg.ItemInfo;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CItemMsg.UseItemResult;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

@MHandler(messageClazz = C2SItemMsg.UseItem.class)
public class MReqUseItemHandler extends MessageHandler {
    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        C2SItemMsg.UseItem msg = (C2SItemMsg.UseItem) getMessage().getData();
        BagManager manager = player.getBagManager();
        List<ItemInfo> goods = new ArrayList<ItemInfo>();
        for (ItemInfo itemInfo : msg.getItemsList()) {
            long id = Long.valueOf(itemInfo.getItemId());
            Item item = manager.getItemCopy(id);
            if (item == null) {
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "背包内根据id没有找到item, id:" + id);
            }
            if (!ItemUtils.canUse(item.getTemplateId())) {
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "该道具不能直接使用, cid:" + item.getTemplateId());
            }
            goods.add(itemInfo);
        }
        // 新获得道具
        Map<Integer, Integer> rewardItems = new HashMap<Integer, Integer>();
        ItemList.Builder itemMsg = ItemList.newBuilder();
        for (ItemInfo itemInfo : goods) {
            // 根据唯一id使用道具
            ItemUtils.doUsebyUid(player, itemInfo.getNum(), Long.valueOf(itemInfo.getItemId()),
                    msg.getCustomParameList(), rewardItems, true, itemMsg);
        }
        if (itemMsg.getItemsCount() > 0 || itemMsg.getEquipmentsCount() > 0) {
            MessageUtils.send(player, itemMsg);// 通知物品变化
        }
        UseItemResult.Builder builder = UseItemResult.newBuilder();
        for (Entry<Integer, Integer> entry : rewardItems.entrySet()) {
            RewardsMsg.Builder b = RewardsMsg.newBuilder();
            b.setId(entry.getKey());
            b.setNum(entry.getValue());
            builder.addItems(b);
        }
        MessageUtils.send(player, builder);// 通知新获得道具
    }
}
