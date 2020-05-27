// package logic.equip.resp;
//
// import org.game.protobuf.s2c.S2CEquipmentMsg;
//
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.robot.RobotThread;
//
// @IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
// order = S2CEquipmentMsg.ChangeSpecialAttrMsg.MsgID.eMsgID_VALUE)
// public class RespEquipChangeArrEvent extends AbstractEvent {
//
// public RespEquipChangeArrEvent(RobotThread robot) {
// super(robot);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
// byte[] data = (byte[]) obj[0];
// if (data.length > 0) {
// S2CEquipmentMsg.ChangeSpecialAttrMsg msg =
// S2CEquipmentMsg.ChangeSpecialAttrMsg.parseFrom(data);
// int oldIndex = msg.getOldAttrIndex();
// int costIndex = msg.getAttrIndexInCostEquipment();
//
// }
// }
//
// }
