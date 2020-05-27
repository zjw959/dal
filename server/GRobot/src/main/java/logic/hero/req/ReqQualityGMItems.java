package logic.hero.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.HeroCfgBean;
import data.bean.HeroProgressCfgBean;
import logic.hero.ReqHeroOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO,
        order = ReqHeroOrder.REQ_QUALITY_GM_ITEMS)
public class ReqQualityGMItems extends AbstractEvent {
    
    public ReqQualityGMItems(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.upQualityHeroId = 0;
        
        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        
        // 随机一个进阶精灵
        Map<Integer, HeroInfo> heroInfos = robotPlayer.getHeros();
        List<HeroInfo> heroInfoList = new ArrayList<>(heroInfos.values());
        int index = RandomUtils.nextInt(heroInfoList.size());
        HeroInfo heroInfo = heroInfoList.get(index);

        int quality = heroInfo.getQuality();
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(heroInfo.getCid());
        int targetQuality = heroCfgBean.getAttribute2() * 100 + quality + 1;
        HeroProgressCfgBean heroProgressCfgBean =
                GameDataManager.getHeroProgressCfgBean(targetQuality);
        
        if (heroProgressCfgBean != null) {
            robot.getPlayer().upQualityHeroId = heroInfo.getCid();
            Map<Integer, Integer> consumeMap = heroProgressCfgBean.getConsume();
            StringBuilder itemBuilder = new StringBuilder();
            for (Map.Entry<Integer, Integer> entry : consumeMap.entrySet()) {
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
                str.append(",");
                
                itemBuilder.append(entry.getKey());
                itemBuilder.append(":");
                itemBuilder.append(entry.getValue());
                itemBuilder.append(",");
            }
        }
        
        if (robot.requestMultipleEvents.containsKey(FunctionType.HERO)) {
            if (robotPlayer.composeHeroId == 0 && robotPlayer.upgradeHeroId == 0
                    && robotPlayer.changeSkinHeroId == 0 && robotPlayer.upQualityHeroId == 0) {
                Log4jManager.getInstance().info(robot.getWindow(),
                        "robot:" + robot.getName() + "精灵  " + "没有精灵可以操作");
                robot.removeCurrentFun();
                super.robotSkipRun();
                return;
            }
        }
        
        String gmContent = str.toString();
        int length = StringUtils.split(gmContent, " ").length;
        if (length > 1) {
            if (robot.getPlayer().upQualityHeroId != 0) {
                C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
                builder.setChannel(1);
                builder.setFun(1);
                builder.setContent(str.toString());
                SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), this.resOrder);
                sendMsg(msg);
            } else {
                super.robotSkipRun();
            }
        } else {
            robotPlayer.upQualityHeroId = 0;
            super.robotSkipRun();
        }
    }

}
