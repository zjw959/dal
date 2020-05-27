package logic.dungeon.handler;

import java.util.List;

import logic.constant.FormationConstant;
import logic.dungeon.bean.ConfigHeroVO;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

/**
 * 副本：英雄限定关卡消息 code = 1808
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.LimitHeroDungeonMsg.class)
public class MLimitHeroDungeonHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MLimitHeroDungeonHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDungeonMsg.LimitHeroDungeonMsg msg =
                (C2SDungeonMsg.LimitHeroDungeonMsg) getMessage().getData();
        int leveId = msg.getLevelId();

        // 限定英雄信息
        List<ConfigHeroVO> configHeros =
                player.getDungeonManager().createDungeonConfigHeroVOs(leveId);
        HeroInfo limitHero;
        S2CDungeonMsg.LimitHeroDungeonMsg.Builder builder =
                S2CDungeonMsg.LimitHeroDungeonMsg.newBuilder();
        S2CDungeonMsg.LimitHeroInfo.Builder limitHeroInfo;
        for (ConfigHeroVO heroVo : configHeros) {
            // 配置英雄
            limitHeroInfo = S2CDungeonMsg.LimitHeroInfo.newBuilder();
            limitHero = heroVo.buildHeroInfo(ChangeType.DEFAULT).build();
            limitHeroInfo.setLimitId(heroVo.getCfgId());
            limitHeroInfo.setHeros(limitHero);
            builder.addHeros(limitHeroInfo.build());
        }
        S2CPlayerMsg.FormationInfo.Builder formation = S2CPlayerMsg.FormationInfo.newBuilder();
        formation.setCt(ChangeType.DEFAULT);
        // 当前类型只有一种
        formation.setType(FormationConstant.TYPE_MAIN);
        // 1为启用
        formation.setStatus(FormationConstant.STATUS_USE);
        for (ConfigHeroVO heroVo : configHeros) {
            formation.addStance(Long.toString(heroVo.getCid()));
        }
        builder.setLeveId(leveId);
        builder.setLimitFormation(formation.build());
        MessageUtils.send(player, builder);
    }
}
