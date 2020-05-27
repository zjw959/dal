package logic.dungeon.bean;

import java.util.List;

/**
 * 玩家副本数据对象
 * 
 * @author Alan
 *
 */
public class DungeonBean {

    /**
     * 关卡cid
     */
    private int cid;

    /**
     * 获得星级（最高星级）
     */
    private int star;

    /**
     * 当日战斗次数
     */
    private int sceneCount;

    /**
     * 是否胜利
     */
    private boolean win;

    /**
     * 达成的目标
     */
    private List<Integer> achieveGoals;

    /**
     * 副本关卡次数刷新时间
     */
    private long lastRefreshTime;

    /**
     * 总战斗次数
     */
    private int totalSceneCount;

    public DungeonBean(int cid, int star, int sceneCount, boolean win, List<Integer> achieveGoals,
            long lastRefreshTime, int totalSceneCount) {
        super();
        this.cid = cid;
        this.star = star;
        this.sceneCount = sceneCount;
        this.win = win;
        this.achieveGoals = achieveGoals;
        this.lastRefreshTime = lastRefreshTime;
        this.totalSceneCount = totalSceneCount;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        if (this.star < star)
            this.star = star;
    }


    public int getSceneCount() {
        return sceneCount;
    }

    public void setSceneCount(int sceneCount) {
        this.sceneCount = sceneCount;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<Integer> getAchieveGoals() {
        return achieveGoals;
    }

    public void setAchieveGoals(List<Integer> achieveGoals) {
        this.achieveGoals = achieveGoals;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public int getTotalSceneCount() {
        return totalSceneCount;
    }

    public void setTotalSceneCount(int totalSceneCount) {
        this.totalSceneCount = totalSceneCount;
    }

    
}
