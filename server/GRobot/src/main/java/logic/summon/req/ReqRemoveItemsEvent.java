// package logic.summon.req;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.apache.commons.lang.StringUtils;
// import org.game.protobuf.c2s.C2SChatMsg;
// import org.game.protobuf.s2c.S2CChatMsg;
// import org.game.protobuf.s2c.S2CItemMsg.DressInfo;
// import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;
// import org.game.protobuf.s2c.S2CItemMsg.ItemInfo;
// import org.game.protobuf.s2c.S2CItemMsg.ItemList;
// import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
//
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.net.message.SMessage;
// import core.robot.RobotThread;
// import data.bean.BaseGoods;
// import data.bean.HeroCfgBean;
// import data.bean.HeroSkinCfgBean;
// import logic.item.GoodsCfgCache;
// import logic.robot.entity.RobotPlayer;
// import logic.summon.ReqSummonOrder;
//
// @IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SUMMON,
// order = ReqSummonOrder.REQ_REMOVE_ITEMS)
// public class ReqRemoveItemsEvent extends AbstractEvent {
//
// public ReqRemoveItemsEvent(RobotThread robot) {
// super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
// RobotPlayer robotPlayer = robot.getPlayer();
//
// robotPlayer.isSummonAward = false;
//
// StringBuilder str = new StringBuilder();
// str.append("./delItemByIds ");
//
// Map<String, Long> delItemMap = new HashMap<>();
// for (ItemList itemList : robotPlayer.summonAward) {
// List<ItemInfo> itemInfos = itemList.getItemsList();
// List<EquipmentInfo> equipmentInfos = itemList.getEquipmentsList();
// List<DressInfo> dressInfos = itemList.getDresssList();
//
// for(ItemInfo itemInfo : itemInfos) {
// BaseGoods baseGood = GoodsCfgCache.getInstance().get(itemInfo.getCid());
// if (baseGood instanceof HeroCfgBean || baseGood instanceof HeroSkinCfgBean) {
// continue;
// }
//
// String entityId = itemInfo.getId();
// if(Long.parseLong(entityId) < Integer.MAX_VALUE) {
// continue;
// }
// if(itemInfo.getCt() == ChangeType.ADD && itemInfo.getNum() > 0) {
// delItemMap.put(itemInfo.getId(), itemInfo.getNum());
// }
// }
//
// for(EquipmentInfo equipmentInfo : equipmentInfos) {
// if(equipmentInfo.getCt() == ChangeType.ADD) {
// delItemMap.put(equipmentInfo.getId(), 1L);
// }
// }
//
// for(DressInfo dressInfo : dressInfos) {
// if(dressInfo.getCt() == ChangeType.ADD) {
// delItemMap.put(dressInfo.getId(), 1L);
// }
// }
// }
//
//
// for(Map.Entry<String, Long> entry : delItemMap.entrySet()) {
// str.append(entry.getKey());
// str.append(":");
// str.append(entry.getValue());
// str.append(",");
// }
// String gmContent = str.toString();
// int length = StringUtils.split(gmContent, " ").length;
// if (length > 1) {
// str.append(" summon");
// C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
// builder.setChannel(1);
// builder.setFun(1);
// builder.setContent(str.toString());
// SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
// builder.build().toByteArray(), this.resOrder);
// sendMsg(msg);
// } else {
// super.robotSkipRun();
// }
// }
//
// }
