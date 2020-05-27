package logic.log.bean;

import log.DalLog;
import log.LoggerConstants;
import logic.character.bean.Player;

public abstract class ActionBaseLog extends BaseLogAdapter {

    private int reason;

    protected ActionBaseLog(Player player, String eventName) {
        super(player, "action", eventName);
    }

    public DalLog getLog() {
        return LoggerConstants.ACTIONEVENT;
    }

    public int getReason() {
        return reason;
    }


    public void setReason(int reason) {
        this.reason = reason;
    }
}
