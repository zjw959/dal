package logic.log.bean;

import log.DalLog;
import log.LoggerConstants;
import logic.character.bean.Player;

public class PlayerBaseLog extends BaseLogAdapter {

    protected PlayerBaseLog(Player player, String eventName) {
        super(player, "role", eventName);
    }

    public DalLog getLog() {
        return LoggerConstants.PLAYEREVENT;
    }
}
