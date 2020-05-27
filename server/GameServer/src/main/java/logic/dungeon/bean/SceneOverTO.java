package logic.dungeon.bean;

import java.util.List;

/**
 * 副本场景结束数据对象
 * 
 * @author Alan
 *
 */
public class SceneOverTO {
    int cid;
    boolean win;
    List<Integer> goals;
    int batter;
    int pickUpTypeCount;
    int pickUpCount;

    public SceneOverTO(int cid, boolean win, List<Integer> goals, int batter, int pickUpTypeCount,
            int pickUpCount) {
        super();
        this.cid = cid;
        this.win = win;
        this.goals = goals;
        this.batter = batter;
        this.pickUpTypeCount = pickUpTypeCount;
        this.pickUpCount = pickUpCount;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<Integer> getGoals() {
        return goals;
    }

    public void setGoals(List<Integer> goals) {
        this.goals = goals;
    }

    public int getBatter() {
        return batter;
    }

    public void setBatter(int batter) {
        this.batter = batter;
    }

    public int getPickUpTypeCount() {
        return pickUpTypeCount;
    }

    public void setPickUpTypeCount(int pickUpTypeCount) {
        this.pickUpTypeCount = pickUpTypeCount;
    }

    public int getPickUpCount() {
        return pickUpCount;
    }

    public void setPickUpCount(int pickUpCount) {
        this.pickUpCount = pickUpCount;
    }

}
