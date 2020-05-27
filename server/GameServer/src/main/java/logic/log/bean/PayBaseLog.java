package logic.log.bean;

import log.DalLog;
import log.LoggerConstants;
import logic.character.bean.Player;

/**
 * 充值日志
 *
 */
public class PayBaseLog extends BaseLogAdapter {
    protected PayBaseLog(Player player, String eventName) {
        super(player, "pay", eventName);
    }

    public DalLog getLog() {
        return LoggerConstants.ACTIONEVENT;
    }
}
