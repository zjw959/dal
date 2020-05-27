package logic.log.bean;

import logic.character.bean.Player;

public class SdkLoginLog extends LoginLog {

    public SdkLoginLog(Player player) {
        super(player, "sdkLogin");
    }
}
