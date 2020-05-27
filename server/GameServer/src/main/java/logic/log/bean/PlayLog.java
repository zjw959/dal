package logic.log.bean;

import logic.character.bean.Player;

/**
 * 小玩法日志 功能-打工、温泉、制作、抽卡、祈愿、洗练
 */
public class PlayLog extends ActionBaseLog {

    private String ext;

    public PlayLog(Player player) {
        super(player,"play");
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

}
