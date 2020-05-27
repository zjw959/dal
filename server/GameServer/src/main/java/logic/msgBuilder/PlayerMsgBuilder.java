package logic.msgBuilder;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.hero.HeroManager;
import logic.hero.bean.Formation;
import logic.hero.bean.Hero;
import logic.playerGuide.PlayerGuideManager;

import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfo;
import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfoList;
import org.game.protobuf.s2c.S2CPlayerMsg.Language;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;
import org.game.protobuf.s2c.S2CPlayerMsg.ResAntiAddictionInfo;
import org.game.protobuf.s2c.S2CPlayerMsg.RespTargetPlayerInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

/**
 * Player消息构建器
 * 
 * @author liujiang
 *
 */
public class PlayerMsgBuilder {

    /** 创建玩家信息 */
    public static PlayerInfo createPlayerInfo(Player player, boolean isFirstLogin) {
        HeroManager heroManager = player.getHeroManager();
        PlayerGuideManager guideManager = player.getPlayerGuideManager();
        PlayerInfo.Builder playerBuilder = PlayerInfo.newBuilder();
        playerBuilder.setPid(player.getPlayerId());
        playerBuilder.setName(player.getPlayerName());
        playerBuilder.setLvl(player.getLevel());
        playerBuilder.setExp(player.getExp());
        playerBuilder.setVipLvl(player.getVipLevel());
        playerBuilder.setVipExp(0);
        playerBuilder.setLanguage(Language.ZH_CN);
        String remark = player.getInfoManager().getRemark();
        playerBuilder.setRemark(remark == null ? "" : remark);
        int helpFightHeroCid = heroManager.getHelpFightHeroCid();
        playerBuilder.setHelpFightHeroCid(helpFightHeroCid);
        if (guideManager.getData() != null) {
            playerBuilder.setClientDiscreteData(guideManager.getData());
        }
        playerBuilder.setAbsorbed(player.getAbsorbed());
        playerBuilder.setGlamour(player.getGlamour());
        playerBuilder.setKnowledge(player.getKnowledge());
        playerBuilder.setFortune(player.getFortune());
        playerBuilder.setTender(player.getTender());
        playerBuilder.setIsFirstLogin(isFirstLogin);// 是否首次登录
        return playerBuilder.build();
    }


    public static S2CPlayerMsg.RespBuyResources buildRespBuyResourcesMsg(int cid, int count) {
        S2CPlayerMsg.RespBuyResources.Builder builder = S2CPlayerMsg.RespBuyResources.newBuilder();
        builder.setCid(cid).setCount(count);
        return builder.build();
    }

    /** 创建PlayerView信息 */
    public static RespTargetPlayerInfo.Builder createPlayerViewBuilder(Player player) {
        RespTargetPlayerInfo.Builder builder = RespTargetPlayerInfo.newBuilder();
        builder.setPlayerInfo(PlayerMsgBuilder.createPlayerInfo(player, false));
        HeroManager heroManager = player.getHeroManager();
        Map<Integer, Formation> formations = heroManager.getFormations();
        Formation formation = formations.get(heroManager.getFormationType());

        FormationInfoList.Builder formationInfoList = FormationInfoList.newBuilder();
        FormationInfo.Builder fbuilder = formation.buildFormationInfo(ChangeType.DEFAULT);

        // 取得英雄
        List<Hero> heros = heroManager.getAllHero();
        for (Hero hero : heros) {
            builder.addHeros(hero.buildHeroInfo(ChangeType.DEFAULT));
        }
        formationInfoList.addFormations(fbuilder);
        builder.setFormationInfo(formationInfoList);
        return builder;
    }

    /**
     * 防沉迷
     */
    public static ResAntiAddictionInfo.Builder buildAntiAddictionMsg(int antiStatus, int onlineHour) {
        ResAntiAddictionInfo.Builder builder = ResAntiAddictionInfo.newBuilder();
        builder.setStatus(antiStatus);
        builder.setTime(onlineHour);
        return builder;
    }

}
