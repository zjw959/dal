package logic.log.bean;

import log.DalLog;
import log.LoggerConstants;
import logic.character.bean.Player;

public class ItemBaseLog extends BaseLogAdapter {

    protected ItemBaseLog(Player player, String eventName) {
        super(player, "item", eventName);
    }

    public DalLog getLog() {
        return LoggerConstants.ITEMEVENT;
    }
}
