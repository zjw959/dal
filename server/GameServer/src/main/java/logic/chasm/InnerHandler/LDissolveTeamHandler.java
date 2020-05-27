package logic.chasm.InnerHandler;

import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.LBaseHandler;

public class LDissolveTeamHandler extends LBaseHandler {

    public LDissolveTeamHandler(Player player) {
        super(player);
    }

    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().dissolveTeam(player);
    }

}
