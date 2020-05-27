package logic.log.bean;

import logic.character.bean.Player;

public class CreateRoleLog extends LoginLog {
    public CreateRoleLog(Player player) {
        super(player,"init");
    }
}
