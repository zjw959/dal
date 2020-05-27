// package logic.equip.req;
//
// import java.util.Map;
//
// import org.game.protobuf.s2c.S2CEquipmentMsg;
// import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;
//
// import core.Log4jManager;
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.robot.RobotThread;
// import logic.equip.ReqEquipOrder;
// import logic.robot.entity.RobotPlayer;
//
// @IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.EQUIP,
// order = ReqEquipOrder.REQ_CHANGE)
// public class ReqEquipChangeArrEvent extends AbstractEvent {
//
// public ReqEquipChangeArrEvent(RobotThread robot) {
// super(robot, S2CEquipmentMsg.ChangeSpecialAttrMsg.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
// RobotPlayer player = robot.getPlayer();
// Map<Integer, Map<String, EquipmentInfo>> eqMap = player.getEquipmentKV();
// if (eqMap.isEmpty()) {
// Log4jManager.getInstance().debug(robot.getWindow(),
// "robot:" + robot.getName() + "装备洗练 " + "装备列表为空");
// super.robotSkipRun();
// return;
// }
// }
//
// }
