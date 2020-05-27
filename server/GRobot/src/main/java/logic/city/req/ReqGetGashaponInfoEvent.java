// package logic.city.req;
//
// import org.game.protobuf.c2s.C2SNewBuildingMsg;
// import core.Log4jManager;
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.net.message.SMessage;
// import core.robot.RobotThread;
// import logic.city.ReqCityOrder;
//
/// ***
// *
// * 获取抓娃娃信息
// *
// * @author
// *
// */
// @IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
// order = ReqCityOrder.REQ_GETGASHAPONINFO)
// public class ReqGetGashaponInfoEvent extends AbstractEvent {
//
// public ReqGetGashaponInfoEvent(RobotThread robot) {
// super(robot, C2SNewBuildingMsg.ReqGetGashaponInfo.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
//
// C2SNewBuildingMsg.ReqGetGashaponInfo.Builder builder =
// C2SNewBuildingMsg.ReqGetGashaponInfo.newBuilder();
//
// SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGetGashaponInfo.MsgID.eMsgID_VALUE,
// builder.build().toByteArray(), resOrder);
// sendMsg(msg, true);
//
// Log4jManager.getInstance().debug(robot.getWindow(),
// "robot:" + robot.getName() + "请求获取抓娃娃信息");
//
//
// }
//
// }
