package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;

import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.chasm.TeamRedisService;
import logic.constant.GameErrorCode;
import logic.hero.HeroManager;
import logic.hero.bean.Hero;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqChangeHero.class)
public class MReqChangeHeroHandler extends MessageHandler {
    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0) {
            // 返回客户端没有队伍
            MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM,
                    player.getPlayerId() + "还没有队伍");
            return;
        }
        C2STeamMsg.ReqChangeHero msg = (C2STeamMsg.ReqChangeHero) getMessage().getData();
        int heroCid = msg.getHeroCid();
        // 判断是否拥有这个英雄
        HeroManager heroManager = player.getHeroManager();
        Hero hero = heroManager.getHero(heroCid);
        if (hero != null) {
            ((PlayerViewService) PlayerViewService.getInstance())
                    .updatePlayerView(player.toPlayerBean(), false, -1);
            int skinCid = hero.getSkin().getSkinItem().getTemplateId();
            TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();
            teamDungeonManager.changeHero(player.getPlayerId(), teamId, heroCid, skinCid);
            MessageUtils.returnEmptyBody();
        } else {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO,
                    player.getPlayerId() + "请求更换英雄" + heroCid + "未找到");
        }
    }
}
