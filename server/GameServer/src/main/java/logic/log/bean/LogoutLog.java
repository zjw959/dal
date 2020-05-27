package logic.log.bean;

import logic.character.bean.Player;

public class LogoutLog extends PlayerBaseLog {

    public LogoutLog(Player player) {
        super(player, "logout");
    }
}
