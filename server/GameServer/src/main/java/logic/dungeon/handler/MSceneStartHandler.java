package logic.dungeon.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.FightStartMsg;

import logic.dungeon.bean.DungeonLimitHeroTO;
import logic.dungeon.bean.SceneStartTO;
import logic.dungeon.scene.SingleDungeonScene;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 副本：战斗开始 code = 1793
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.FightStartMsg.class)
public class MSceneStartHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSceneStartHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDungeonMsg.FightStartMsg msg = (C2SDungeonMsg.FightStartMsg) getMessage().getData();
        int cid = msg.getLevelCid();
        int quickBattleCount = 0;
        boolean isDuelMod = false;
        if (msg.hasQuickCount())
            quickBattleCount = msg.getQuickCount();
        if (msg.hasIsDuelMod())
            isDuelMod = msg.getIsDuelMod();
        int helpPlayerId = msg.getHelpPlayerId();
        int helpHeroCid = msg.getHelpHeroCid();
        // 英雄限定新逻辑
        List<DungeonLimitHeroTO> limitedHeros = null;
        for (C2SDungeonMsg.LimitedHeroInfo info : msg.getLimitHerosList()) {
            if (limitedHeros == null)
                limitedHeros = new ArrayList<DungeonLimitHeroTO>();
            limitedHeros.add(new DungeonLimitHeroTO(info.getLimitType(), info.getLimitCid()));
        }
        // 组装数据
        SceneStartTO startTo =
                new SceneStartTO(cid, quickBattleCount, isDuelMod, helpPlayerId, helpHeroCid,
                        limitedHeros);
        // 逻辑模块调用
        SingleDungeonScene dungeonFight = player.getDungeonManager().sceneStart(startTo);
        // 组装返回数据
        FightStartMsg.Builder builder = DungeonMsgBuilder.getSceneStartMsg(dungeonFight);
        // 如果有限定英雄信息
        if (limitedHeros != null) {
            for (DungeonLimitHeroTO dungeonLimitHeroTO : limitedHeros) {
                S2CDungeonMsg.LimitHeroSimpleInfo.Builder simple =
                        S2CDungeonMsg.LimitHeroSimpleInfo.newBuilder();
                simple.setLimitCid(dungeonLimitHeroTO.getCid());
                simple.setLimitType(dungeonLimitHeroTO.getType());
                builder.addLimitHeros(simple.build());
            }
        }
        MessageUtils.send(player, builder);
    }
}
