package logic.city.req;

import java.util.Map;
import java.util.Map.Entry;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import logic.city.ReqCityOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GM_ITEMS)
public class ReqCityGMItemsEvent extends AbstractEvent {

    public ReqCityGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        str.append(500005);
        str.append(":");
        str.append(1000000);
        str.append(",");

        GameDataManager.getFoodbaseCfgBeans().forEach((bean) -> {
            checkAdd(robot.getPlayer(), bean.getAbility(), str);
        });

        GameDataManager.getFoodbaseCfgBeans().forEach((bean) -> {
            checkAdd(robot.getPlayer(), bean.getMaterials(), str);
        });

        GameDataManager.getHandworkbaseCfgBeans().forEach((bean) -> {
            checkAdd(robot.getPlayer(), bean.getAbility(), str);
        });

        GameDataManager.getHandworkbaseCfgBeans().forEach((bean) -> {
            checkAdd(robot.getPlayer(), bean.getMaterials(), str);
        });

        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }

    /** 检测 **/
    private void checkAdd(RobotPlayer player, Map<Integer, Integer> map, StringBuilder str) {
        if (map == null || map.size() <= 0)
            return;
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            if (player.getItemCount(entry.getKey()) < 200) {
                str.append((entry.getKey()));
                str.append(":");
                str.append(200);
                str.append(",");
            }
        }
    }
}
