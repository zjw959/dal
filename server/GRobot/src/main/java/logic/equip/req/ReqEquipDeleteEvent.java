// package logic.equip.req;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Map.Entry;
//
// import org.game.protobuf.c2s.C2SChatMsg;
// import org.game.protobuf.s2c.S2CChatMsg;
// import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;
//
// import core.Log4jManager;
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.net.message.SMessage;
// import core.robot.RobotThread;
// import logic.equip.ReqEquipOrder;
// import logic.robot.entity.RobotPlayer;
//
// @IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
// order = ReqEquipOrder.REQ_EQUIP_DELETE)
// public class ReqEquipDeleteEvent extends AbstractEvent {
//
// public ReqEquipDeleteEvent(RobotThread robot) {
// super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
// RobotPlayer player = robot.getPlayer();
// Map<String, EquipmentInfo> eqMap = player.getEquipmentKV();
// if (eqMap.isEmpty()) {
// Log4jManager.getInstance().debug(robot.getWindow(),
// "robot:" + robot.getName() + "删除装备 " + "装备列表为空");
// super.robotSkipRun();
// return;
// }
// List<String> selectInfo = new ArrayList<>(eqMap.keySet());
// // 装备
// StringBuilder str = new StringBuilder();
// Map<String, Integer> map = new HashMap<>();
// str.append("./delItemByIds ");
// for (String id : selectInfo) {
// EquipmentInfo info = eqMap.get(id);
// if (info.getPosition() != 0 && !info.getHeroId().equals("0")) {
// continue;
// }
// map.put(info.getId(), 1);
// }
// if (map.isEmpty()) {
// Log4jManager.getInstance().debug(robot.getWindow(),
// "robot:" + robot.getName() + "删除装备 " + "删除列表为空");
// super.robotSkipRun();
// return;
// }
// for (Entry<String, Integer> entry : map.entrySet()) {
// str.append(entry.getKey()).append(":").append(String.valueOf(entry.getValue()))
// .append(",");
// }
// str.append(" ReqEquipDeleteEvent");
//
// String context = str.toString();
// C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
// builder.setChannel(1);
// builder.setFun(1);
// builder.setContent(context);
// SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
// builder.build().toByteArray(), this.resOrder);
// sendMsg(msg);
// Log4jManager.getInstance().debug(robot.getWindow(),
// "robot:" + robot.getName() + "装备删除:" + "true");
// }
//
// }
