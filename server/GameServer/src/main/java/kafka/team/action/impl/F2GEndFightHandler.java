package kafka.team.action.impl;

import org.apache.log4j.Logger;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GEndFightParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.InnerHandler.LEndFightHandler;
import logic.constant.EReason;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import thread.log.LogProcessor;
import thread.player.PlayerProcessorManager;
import utils.ExceptionEx;

public class F2GEndFightHandler implements TeamActionHandler<F2GEndFightParam> {
    private static final Logger LOGGER = Logger.getLogger(F2GEndFightHandler.class);
    
    @Override
    public void process(F2GEndFightParam param) {
        int playerId = param.getPlayerId();
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if(player != null && player.isOnline()) {
            PlayerProcessorManager.getInstance().addPlayerHandler(player, new LEndFightHandler(player, param));
        } else {
            LogicScriptsUtils.getChasmScript().endFightOffline(playerId, param);
        }
        
        if(player != null) {
            try {
                LogProcessor.getInstance().sendLog(LogBeanFactory.createBattleLog(player, param.getDungeonCid(), param.isWin() ? 1 : 0, null, EReason.CHASM_THROUGH.value(), String.valueOf(param.getTeamId())));
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

}
