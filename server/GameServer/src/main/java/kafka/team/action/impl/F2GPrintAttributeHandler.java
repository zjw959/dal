package kafka.team.action.impl;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CHeroMsg.ResPropertyChange;
import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GPrintAttributeHandlerParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.hero.HeroProperty;
import utils.FileEx;

public class F2GPrintAttributeHandler implements TeamActionHandler<F2GPrintAttributeHandlerParam> {
    private static final Logger LOGGER = Logger.getLogger(F2GPrintAttributeHandler.class);
    @Override
    public void process(F2GPrintAttributeHandlerParam json) {
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        HeroProperty heroProperty =
                player.getHeroManager().getHero(json.getHeroCid()).getHeroProperty();
        ResPropertyChange.Builder propertyChangeBuilder = ResPropertyChange.newBuilder();
        propertyChangeBuilder.setHeroId(String.valueOf(json.getHeroCid()));
        propertyChangeBuilder.addAllAttr(heroProperty.getAttributeInfoBuilder());
        propertyChangeBuilder.setFightPower(heroProperty.calculateFightPower());
        StringBuilder fileName = new StringBuilder();
        fileName.append("./logs/win" + json.getTeamId() + "_" + json.getPlayerId());
        try {
            FileEx.write(fileName.toString(), propertyChangeBuilder.build().toByteArray());
        } catch (IOException e) {
            LOGGER.error("isSameMemberWin=false print error!fileName=" + fileName.toString(), e);
        }
    }

}
