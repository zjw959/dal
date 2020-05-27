// package logic.summon.req;
//
// import org.game.protobuf.c2s.C2SChatMsg;
// import org.game.protobuf.s2c.S2CChatMsg;
//
// import core.event.AbstractEvent;
// import core.event.EventType;
// import core.event.FunctionType;
// import core.event.IsEvent;
// import core.net.message.SMessage;
// import core.robot.RobotThread;
// import logic.summon.ReqSummonOrder;
//
// @IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.SUMMON,
// order = ReqSummonOrder.REQ_REMOVE_UNUSEDITEMS)
// public class ReqRemoveUnUsedItemsEvent extends AbstractEvent {
//
// public ReqRemoveUnUsedItemsEvent(RobotThread robot) {
// super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
// }
//
// @Override
// public void action(Object... obj) throws Exception {
// StringBuilder str = new StringBuilder();
// str.append("./cleanUnUsedItems ");
//
// C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
// builder.setChannel(1);
// builder.setFun(1);
// builder.setContent(str.toString());
// SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
// builder.build().toByteArray(), this.resOrder);
// sendMsg(msg);
// }
//
// }
