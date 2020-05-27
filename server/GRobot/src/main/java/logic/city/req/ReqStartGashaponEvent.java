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
// * 请求抓娃娃
// *
// * @author
// *
// */
// @IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
// order = ReqCityOrder.REQ_START_GASHAPON)
// public class ReqStartGashaponEvent extends AbstractEvent {
//
// public ReqStartGashaponEvent(RobotThread robot) {
// super(robot, C2SNewBuildingMsg.ReqStartGashapon.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
//
// C2SNewBuildingMsg.ReqStartGashapon.Builder builder =
// C2SNewBuildingMsg.ReqStartGashapon.newBuilder();
//
// SMessage msg = new SMessage(C2SNewBuildingMsg.ReqStartGashapon.MsgID.eMsgID_VALUE,
// builder.build().toByteArray(), resOrder);
// sendMsg(msg, true);
//
// Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "请求开始抓娃娃");
//
//
// }
//
// }
