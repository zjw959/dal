package logic.bag.handler;

import java.util.List;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.ItemConstantId;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.msgBuilder.ItemMsgBuilder;
import logic.role.bean.RoleTouch;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;
import org.game.protobuf.c2s.C2SItemMsg;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

@MHandler(messageClazz = C2SItemMsg.GetItems.class)
public class MReqGetItemsHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        BagManager manager = player.getBagManager();
        List<Item> items = manager.getAllItems();
        // 为兼容以前的设计结构，保持协议不变，单独封装代币为道具，以前的代币是进入背包存储的
        List<Item> newitems = null;
        // 金币
        if (player.getGold() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.GOLD, (int) player.getGold());
            items.addAll(newitems);
        }
        // 钻石
        if (player.getToltalDiamond() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.SYSTEM_DIAMOND,
                    (int) player.getToltalDiamond());
            items.addAll(newitems);
        }
        // 体力
        int strength = player.getStrength();
        if (strength > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.STRENGTH, strength);
            items.addAll(newitems);
        }
        // 友情点
        int friendPoint = player.getFriendManager().getFriendPoint();
        if (friendPoint > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.FRIEND_SHIP_POINT, friendPoint);
            items.addAll(newitems);
        }
        // 每日看板娘触摸次数
        RoleTouch roleTouch = player.getRoleManager().getRoleTouch();
        if (roleTouch != null) {
            newitems = ItemUtils.createItems(ItemConstantId.DAILY_TOUCH_ROLE_TIMES,
                    roleTouch.getTouchTimes());
            items.addAll(newitems);
        }
        // 抓娃娃次数
        if (player.getCityInfoManager()
                .getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT) > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.DAILY_GASHAPON_COUNT, player
                    .getCityInfoManager().getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT));
            items.addAll(newitems);
        }
        // 天空币
        if (player.getCityInfoManager().getCityPackageById(ItemConstantId.SKY_COIN) > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.SKY_COIN,
                    player.getCityInfoManager().getCityPackageById(ItemConstantId.SKY_COIN));
            items.addAll(newitems);
        }
        // 城市精力
        if (player.getCityInfoManager().getCityPackageById(ItemConstantId.CITY_ENERGY) > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.CITY_ENERGY,
                    player.getCityInfoManager().getCityPackageById(ItemConstantId.CITY_ENERGY));
            items.addAll(newitems);
        }
        // 日常约会次数
        if (player.getDatingManager().getDailyCont() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.DAILY_DATING_COUNT,
                    player.getDatingManager().getDailyCont());
            items.addAll(newitems);
        }
        // 日常活跃度
        int actEngrty = player.getTaskManager().getActEngrty();
        if (actEngrty > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.PLAYER_ACTIVE, actEngrty);
            items.addAll(newitems);
        }
        // 专注
        if (player.getAbsorbed() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.PLAYER_ABSORBED, player.getAbsorbed());
            items.addAll(newitems);
        }
        // 魅力
        if (player.getGlamour() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.PLAYER_GLAMOUR, player.getGlamour());
            items.addAll(newitems);
        }
        // 温柔
        if (player.getTender() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.PLAYER_TENDER, player.getTender());
            items.addAll(newitems);
        }
        // 知识
        if (player.getKnowledge() > 0) {
            newitems =
                    ItemUtils.createItems(ItemConstantId.PLAYER_KNOWLEDGE, player.getKnowledge());
            items.addAll(newitems);
        }
        // 幸运
        if (player.getFortune() > 0) {
            newitems = ItemUtils.createItems(ItemConstantId.PLAYER_FORTUNE, player.getFortune());
            items.addAll(newitems);
        }

        ItemList.Builder builder = ItemMsgBuilder.createItemList(ChangeType.DEFAULT, items);
        MessageUtils.send(player, builder);
    }
}
