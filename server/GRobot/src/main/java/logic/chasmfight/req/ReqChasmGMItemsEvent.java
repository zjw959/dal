package logic.chasmfight.req;

import java.util.Map;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChasmMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.ChasmDungeonCfgBean;
import logic.chasmfight.ReqChasmFightOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM_FIGHT,
        order = ReqChasmFightOrder.REQ_GM_ITEMS)
public class ReqChasmGMItemsEvent extends AbstractEvent {

    public ReqChasmGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.chasmDungeonId = 0;
        
        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        str.append(500005);
        str.append(":");
        str.append(1000000);
        str.append(",");
        
        Map<Integer, S2CChasmMsg.ChasmInfo> chasmInfos = robotPlayer.getChasmInfos();
        for(Map.Entry<Integer, S2CChasmMsg.ChasmInfo> entry : chasmInfos.entrySet()) {
            S2CChasmMsg.ChasmInfo chasmInfo = entry.getValue();
            int id = chasmInfo.getId();
            int status = chasmInfo.getStatus();
            int fightCount = chasmInfo.getFightCount();
            ChasmDungeonCfgBean chasmDungeonCfgBean = GameDataManager.getChasmDungeonCfgBean(id);
            if(status == 1 && fightCount < chasmDungeonCfgBean.getFightCount()) {
                robotPlayer.chasmDungeonId = id;
                Map<Integer, Integer> fightCost = chasmDungeonCfgBean.getFightCost();
                for(Map.Entry<Integer, Integer> temp : fightCost.entrySet()) {
                    str.append(temp.getKey());
                    str.append(":");
                    str.append(temp.getValue());
                    str.append(",");
                }
                break;
            }
        }
        
        if (robot.requestMultipleEvents.containsKey(FunctionType.CHASM_FIGHT)) {
            if (robotPlayer.chasmDungeonId == 0) {
                Log4jManager.getInstance().info(robot.getWindow(),
                        "robot:" + robot.getName() + "组队副本  " + "没有关卡可以打");
                robot.requestMultipleEvents.remove(FunctionType.CHASM_FIGHT);
            }
        }
        
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }
}
