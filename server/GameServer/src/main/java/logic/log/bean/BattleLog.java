package logic.log.bean;

import logic.character.bean.Player;

/**
 * 作战日志
 */
public class BattleLog extends ActionBaseLog {

    private int battleId;
    private int result;
    private String teamInfo;
    private String ext;

    public BattleLog(Player player) {
        super(player, "battle");
    }

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getTeamInfo() {
        return teamInfo;
    }

    public void setTeamInfo(String teamInfo) {
        this.teamInfo = teamInfo;
    }

}
