package logic.chasm.req;

import java.util.Map;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.HeroCfgBean;
import logic.chasm.ReqChasmOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM,
        order = ReqChasmOrder.REQ_GM_ITEMS)
public class ReqChasmGMItemsEvent extends AbstractEvent {

    public ReqChasmGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.isComposeHeroOne = false;
        robotPlayer.isComposeHeroTwo = false;
//        robotPlayer.chasmDungeonId = 0;
        
        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        str.append(500005);
        str.append(":");
        str.append(1000000);
        str.append(",");
        
        Map<Integer, HeroInfo> heros = robotPlayer.getHeros();
        if(!heros.containsKey(robotPlayer.composeHeroOne)) {
            robotPlayer.isComposeHeroOne = true;
            HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(robotPlayer.composeHeroOne);
            Map<Integer, Integer> composeItem = heroCfgBean.getCompose();
            for(Map.Entry<Integer, Integer> entry : composeItem.entrySet()) {
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
                str.append(",");
            }
        }
        
        if(!heros.containsKey(robotPlayer.composeHeroTwo)) {
            robotPlayer.isComposeHeroTwo = true;
            HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(robotPlayer.composeHeroTwo);
            Map<Integer, Integer> composeItem = heroCfgBean.getCompose();
            for(Map.Entry<Integer, Integer> entry : composeItem.entrySet()) {
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
                str.append(",");
            }
        }
        
//        int randomNum = RandomUtils.nextInt(3);
//        if(randomNum == 0) {
//            robotPlayer.isCreateTeam = true;
//        }
//        
//        List<Integer> dungeonIds = new ArrayList<>();
//        Map<Integer, S2CChasmMsg.ChasmInfo> chasmInfos = robotPlayer.getChasmInfos();
//        for(Map.Entry<Integer, S2CChasmMsg.ChasmInfo> entry : chasmInfos.entrySet()) {
//            S2CChasmMsg.ChasmInfo chasmInfo = entry.getValue();
//            int id = chasmInfo.getId();
//            int status = chasmInfo.getStatus(); 
//            int fightCount = chasmInfo.getFightCount();
//            ChasmDungeonCfgBean chasmDungeonCfgBean = GameDataManager.getChasmDungeonCfgBean(id);
//            if(status == 1 && fightCount < chasmDungeonCfgBean.getFightCount()) {
//                dungeonIds.add(id);
//            }
//        }
//        
//        if(dungeonIds.size() > 0) {
//            int index = RandomUtils.nextInt(dungeonIds.size());
//            robotPlayer.chasmDungeonId = dungeonIds.get(index);
//        
//            ChasmDungeonCfgBean chasmDungeonCfgBean = GameDataManager.getChasmDungeonCfgBean(robotPlayer.chasmDungeonId);
//            Map<Integer, Integer> fightCost = chasmDungeonCfgBean.getFightCost();
//            for(Map.Entry<Integer, Integer> entry : fightCost.entrySet()) {
//                str.append(entry.getKey());
//                str.append(":");
//                str.append(entry.getValue());
//                str.append(",");
//            }
//        }
        
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }
}
