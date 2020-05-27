package logic.hero.req;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
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
import data.bean.DiscreteDataCfgBean;
import data.bean.HeroCfgBean;
import data.bean.LevelUpCfgBean;
import logic.hero.ReqHeroOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO,
        order = ReqHeroOrder.REQ_GM_ITEMS)
public class ReqHeroGMItemsEvent extends AbstractEvent {

    public ReqHeroGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.composeHeroId = 0;
        robotPlayer.upgradeHeroId = 0;
        robotPlayer.heroExpItem = 0;
        robotPlayer.changeSkinHeroId = 0;
        robotPlayer.changeSkinId = 0;

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        str.append(500005);
        str.append(":");
        str.append(1000000);
        str.append(",");
        
        // 随机一个合成英雄
        Map<Integer, HeroInfo> heroInfos = robotPlayer.getHeros();
        List<HeroCfgBean> randomCfgBeans = new ArrayList<>();
        List<HeroCfgBean> heroCfgBeans = GameDataManager.getHeroCfgBeans();
        for (HeroCfgBean temp : heroCfgBeans) {
            if (!heroInfos.containsKey(temp.getId())) {
                randomCfgBeans.add(temp);
            }
        }
        if (randomCfgBeans.size() > 0) {
            int index = RandomUtils.nextInt(randomCfgBeans.size());
            HeroCfgBean heroCfgBean = randomCfgBeans.get(index);
            robot.getPlayer().composeHeroId = heroCfgBean.getId();
            // 请求加入英雄碎片
            Map<Integer, Integer> composeMap = heroCfgBean.getCompose();
            for (Map.Entry<Integer, Integer> entry : composeMap.entrySet()) {
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
                str.append(",");
            }
        }

        // 随机一个升级英雄
        List<HeroInfo> heroInfoList = new ArrayList<>(heroInfos.values());
        int index = RandomUtils.nextInt(heroInfoList.size());
        HeroInfo heroInfo = heroInfoList.get(index);
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(heroInfo.getCid());
        
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(9002);
        Map<String, Integer> data = discreteDataCfgBean.getData();
        int maxLvl = data.get("hmaxlvl");
        int level = heroInfo.getLvl();
        LevelUpCfgBean nextLevelCfgBean = GameDataManager.getLevelUpCfgBean(level + 1);

        if (nextLevelCfgBean != null && level < maxLvl) {
            robot.getPlayer().upgradeHeroId = heroInfo.getCid();
            int[] expItems = heroCfgBean.getExpitem();
            index = RandomUtils.nextInt(expItems.length);
            int expItem = expItems[index];
            robot.getPlayer().heroExpItem = expItem;
            str.append(expItem);
            str.append(":");
            str.append(1);
            str.append(",");
        }

        // 随机一个换皮英雄
        List<Integer> randomSkinList = new ArrayList<>();
        int skinCid = heroInfo.getSkinCid();
        int[] optionalSkin = heroCfgBean.getOptionalSkin();
        for (int temp : optionalSkin) {
            if (temp != skinCid) {
                randomSkinList.add(temp);
            }
        }
        if (randomSkinList.size() > 0) {
            robot.getPlayer().changeSkinHeroId = heroInfo.getCid();
            index = RandomUtils.nextInt(randomSkinList.size());
            int randomSkin = randomSkinList.get(index);
            robot.getPlayer().changeSkinId = randomSkin;
            Map<Integer, Integer> itemMap = new HashMap<>();
            itemMap.put(randomSkin, 1);
            if (!robotPlayer.isEnoughItem(itemMap)) {
                str.append(randomSkin);
                str.append(":");
                str.append(1);
                str.append(",");
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
