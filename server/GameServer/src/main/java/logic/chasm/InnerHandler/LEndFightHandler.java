package logic.chasm.InnerHandler;

import kafka.team.param.f2g.F2GEndFightParam;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.LBaseHandler;

public class LEndFightHandler extends LBaseHandler {
    private F2GEndFightParam param;
    
    public LEndFightHandler(Player player, F2GEndFightParam param) {
        super(player);
        this.param = param;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().endFightOnline(player, param);
    }

}
