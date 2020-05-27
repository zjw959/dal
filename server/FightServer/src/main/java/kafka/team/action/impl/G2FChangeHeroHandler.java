package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ChangeHeroParam;
import logic.support.LogicScriptsUtils;

public class G2FChangeHeroHandler implements TeamActionHandler<ChangeHeroParam> {

    @Override
    public void process(ChangeHeroParam json) {
        LogicScriptsUtils.getChasmScript().changeHero(json);
    }

}
