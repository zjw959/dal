package logic.store.req;

import java.util.List;

import org.game.protobuf.c2s.C2SStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.StoreCfgBean;
import logic.store.ReqStoreOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.STORE,
        order = ReqStoreOrder.REQ_GET_STORE_LIST)
public class ReqStoreListEvent extends AbstractEvent {

    public ReqStoreListEvent(RobotThread robot) {
        super(robot, S2CStoreMsg.StoreDataInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        List<StoreCfgBean> configList = GameDataManager.getStoreCfgBeans();
        C2SStoreMsg.GetStoreInfo.Builder builder = C2SStoreMsg.GetStoreInfo.newBuilder();
        for (StoreCfgBean storeCfgBean : configList) {
            builder.addCid(storeCfgBean.getId());
        }
        SMessage msg = new SMessage(C2SStoreMsg.GetStoreInfo.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
