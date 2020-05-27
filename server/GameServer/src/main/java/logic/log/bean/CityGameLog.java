package logic.log.bean;

import logic.character.bean.Player;

/***
 * 
 * 城市小游戏
 * 
 * @author lihongji
 *
 */
public class CityGameLog extends ActionBaseLog {

    public CityGameLog(Player player) {
        super(player, "cityGame");
    }

    /** 游戏id **/
    private int gameId;
    /** 时间 **/
    private long time;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



}
