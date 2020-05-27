package logic.dungeon.req;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_DUNGEON_START)
public class ReqDungeonStartInfo extends AbstractEvent {

    public ReqDungeonStartInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.FightStartMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // 获取可用的
        List<Integer> ids = new ArrayList<Integer>(robot.getPlayer().getDungeons().keySet());
        if (ids.size() <= 0) {
            // 请求order列表
            List<Integer> reqList = null;
            if (robot.getReqQueue() != null) {
                reqList = robot.getReqQueue().stream().map(event -> {
                    IsEvent isEventClazz = event.getClass().getAnnotation(IsEvent.class);
                    return isEventClazz.order();
                }).collect(Collectors.toList());
            }
            Log4jManager.getInstance().warn(
                    robot.getWindow(),
                    "exception null dungeonlevel list, reqOrders " + reqList + ", repsId:"
                            + robot.getResOrder() + ", levelCid:"
                            + robot.getPlayer().getDungeonLevelId());
            super.robotSkipRun();
            return;
        }
        int chosenId = ids.get((int) (Math.random() * ids.size()));

        C2SDungeonMsg.FightStartMsg.Builder build = C2SDungeonMsg.FightStartMsg.newBuilder();
        build.setLevelCid(chosenId);
        build.setHelpHeroCid(0);
        build.setHelpPlayerId(0);
        List<C2SDungeonMsg.LimitedHeroInfo> limitedHeros =
                new ArrayList<C2SDungeonMsg.LimitedHeroInfo>();
        // 如果有限制
        DungeonLevelCfgBean cfg = GameDataManager.getDungeonLevelCfgBean(chosenId);
        if (cfg.getHeroLimitID() != null && cfg.getHeroLimitID().length > 0) {
            for (int limitId : cfg.getHeroLimitID()) {
                limitedHeros.add(C2SDungeonMsg.LimitedHeroInfo.newBuilder().setLimitCid(limitId)
                        .setLimitType(2).build());
            }
        }
        // 如果有禁用
        if (cfg.getHeroForbiddenID() != null && cfg.getHeroForbiddenID().length > 0
                && robot.getPlayer().getHeros().size() > 0) {
            LinkedList<HeroInfo> heros = new LinkedList<>(robot.getPlayer().getHeros().values());
            Collections.shuffle(heros);
            while (heros.size() > 0 && limitedHeros.size() < 3) {
                int currentID = heros.poll().getCid();
                boolean addable = true;
                for (int fid : cfg.getHeroForbiddenID()) {
                    if (currentID != fid)
                        continue;
                    addable = false;
                    break;
                }
                if (addable)
                    limitedHeros.add(C2SDungeonMsg.LimitedHeroInfo.newBuilder()
                            .setLimitCid(currentID).setLimitType(1).build());
            }
        }
        build.addAllLimitHeros(limitedHeros);
        SMessage msg =
                new SMessage(C2SDungeonMsg.FightStartMsg.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg);
    }
}
