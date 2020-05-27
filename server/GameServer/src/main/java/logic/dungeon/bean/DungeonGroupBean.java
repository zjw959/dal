package logic.dungeon.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 玩家副本组数据对象
 * 
 * @author Alan
 *
 */
public class DungeonGroupBean {

    /**
     * 战斗次数
     */
    private int sceneCount;

    /**
     * 购买次数
     */
    private int buyCount;

    /**
     * 上次刷新时间
     */
    private long lastRefreshTime;

    /**
     * 已领奖励
     */
    private Map<Integer, Set<Integer>> getedReward;

    /**
     * 配置id
     */
    private int cid;

    /**
     * 副本组当前主线剧情id
     */
    private int lastMainLine;

    /**
     * 最大主线进度
     */
    private int maxMainLine;
    /**
     * 总星数
     */
    private Map<Integer, Integer> stars = new HashMap<Integer, Integer>();
    /**
     * 总心数
     */
    private Map<Integer, Integer> hearts = new HashMap<Integer, Integer>();
    /**
     * 多倍奖励累计值
     */
    private int multipleRewardCount;

    public DungeonGroupBean(int sceneCount, int buyCount, long lastRefreshTime,
            Map<Integer, Set<Integer>> getedReward, int cid, int lastMainLine, int maxMainLine) {
        super();
        this.sceneCount = sceneCount;
        this.buyCount = buyCount;
        this.lastRefreshTime = lastRefreshTime;
        this.getedReward = getedReward;
        this.cid = cid;
        this.lastMainLine = lastMainLine;
        this.maxMainLine = maxMainLine;
    }

    public int getSceneCount() {
        return sceneCount;
    }

    public void setSceneCount(int sceneCount) {
        this.sceneCount = sceneCount;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public Map<Integer, Set<Integer>> getGetedReward() {
        return getedReward;
    }

    public void setGetedReward(Map<Integer, Set<Integer>> getedReward) {
        this.getedReward = getedReward;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getLastMainLine() {
        return lastMainLine;
    }

    public void setLastMainLine(int lastMainLine) {
        this.lastMainLine = lastMainLine;
    }

    public int getMaxMainLine() {
        return maxMainLine;
    }

    public void setMaxMainLine(int maxMainLine) {
        this.maxMainLine = maxMainLine;
    }

    public Map<Integer, Integer> getStars() {
        return stars;
    }

    public Map<Integer, Integer> getHearts() {
        return hearts;
    }

    public int getMultipleRewardCount() {
        return multipleRewardCount;
    }

    public void setMultipleRewardCount(int multipleRewardCount) {
        this.multipleRewardCount = multipleRewardCount;
    }
    
}
