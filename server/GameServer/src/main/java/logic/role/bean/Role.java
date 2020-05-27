package logic.role.bean;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import utils.ToolMap;

/**
 * 
 * @Description 看板娘
 * @author LiuJiang
 * @date 2018年6月13日 上午11:45:52
 *
 */
@SuppressWarnings("rawtypes")
public class Role {

    /**
     * 配置模板ID
     */
    private int cid;

    /**
     * 好感度经验值
     */
    private int favor;

    /**
     * 心情值
     */
    private int mood;

    /**
     * 解锁礼品
     */
    private List<Integer> unlockGift;

    /**
     * 解锁爱好
     */
    private List<Integer> unlockHobby;

    /**
     * 时装约会
     */
    private List dressDating;

    /**
     * 赠送礼物约会
     */
    private Map donateDating;

    /**
     * 上次心情值刷新时间
     */
    private long lastMoodUpdateTime;

    /**
     * 好感度临界点，true则不能继续提升好感度
     */
    private boolean criticalPoint;

    /**
     * 触发约会
     */
    private List triggerDating;

    /**
     * 当日城市约会次数
     */
    private int todayCityDatingCount;

    /**
     * 上次刷新时间
     */
    private long lastRefreshTime;

    /**
     * 当前使用时装唯一id
     */
    private long dressId;
    /**
     * 当前使用装饰房间唯一id
     */
    private long roomId;

    /** 效果 */
    private Map<Integer, Integer> effectMap = Maps.newHashMap();

    /**精灵状态**/
    private RoleState state=new RoleState();
    
    /**喂食精灵时间**/
    private long feedFoodTime;
//    /**精灵送礼时间**/
//    private long feedGiftTime;
    /**日常约会时间**/
    private long dailyTime;
    
    /**喜欢的礼物或者食物**/
    private List<Integer> favoriteIds;
    
    public Role(int cid, int favor, int mood, List<Integer> unlockGift, List<Integer> unlockHobby,
            List dressDating, Map donateDating, long lastMoodUpdateTime, boolean criticalPoint,
            List triggerDating, int todayCityDatingCount, long lastRefreshTime,List<Integer> favoriteIds) {
        super();
        this.cid = cid;
        this.favor = favor;
        this.mood = mood;
        this.unlockGift = unlockGift;
        this.unlockHobby = unlockHobby;
        this.dressDating = dressDating;
        this.donateDating = donateDating;
        this.lastMoodUpdateTime = lastMoodUpdateTime;
        this.criticalPoint = criticalPoint;
        this.triggerDating = triggerDating;
        this.todayCityDatingCount = todayCityDatingCount;
        this.lastRefreshTime = lastRefreshTime;
        this.favoriteIds=favoriteIds;
    }


    public Role(int cid, int favor) {
        super();
        this.cid = cid;
        this.favor = favor;
    }


    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getFavor() {
        return favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public List<Integer> getUnlockGift() {
        return unlockGift;
    }

    public void setUnlockGift(List<Integer> unlockGift) {
        this.unlockGift = unlockGift;
    }

    public List<Integer> getUnlockHobby() {
        return unlockHobby;
    }

    public void setUnlockHobby(List<Integer> unlockHobby) {
        this.unlockHobby = unlockHobby;
    }

    public List getDressDating() {
        return dressDating;
    }

    public void setDressDating(List dressDating) {
        this.dressDating = dressDating;
    }

    public Map getDonateDating() {
        return donateDating;
    }

    public void setDonateDating(Map donateDating) {
        this.donateDating = donateDating;
    }

    public boolean isCriticalPoint() {
        return criticalPoint;
    }

    public void setCriticalPoint(boolean criticalPoint) {
        this.criticalPoint = criticalPoint;
    }

    public List getTriggerDating() {
        return triggerDating;
    }

    public void setTriggerDating(List triggerDating) {
        this.triggerDating = triggerDating;
    }

    public int getTodayCityDatingCount() {
        return todayCityDatingCount;
    }

    public void setTodayCityDatingCount(int todayCityDatingCount) {
        this.todayCityDatingCount = todayCityDatingCount;
    }

    public long getLastMoodUpdateTime() {
        return lastMoodUpdateTime;
    }

    public void setLastMoodUpdateTime(long lastMoodUpdateTime) {
        this.lastMoodUpdateTime = lastMoodUpdateTime;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public long getDressId() {
        return dressId;
    }

    public void setDressId(long dressId) {
        this.dressId = dressId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Map<Integer, Integer> getEffectMap() {
        return effectMap;
    }

    public void setEffectMap(Map<Integer, Integer> effectMap) {
        this.effectMap = effectMap;
    }

    public int getEffectByType(int type) {
        return ToolMap.getInt(type, effectMap, 0);
    }

    public RoleState getState() {
        return state;
    }

    public void setState(RoleState state) {
        this.state = state;
    }

    
    public long getFeedFoodTime() {
        return feedFoodTime;
    }


    public void setFeedFoodTime(long feedFoodTime) {
        this.feedFoodTime = feedFoodTime;
    }


//    public long getFeedGiftTime() {
//        return feedGiftTime;
//    }
//
//
//    public void setFeedGiftTime(long feedGiftTime) {
//        this.feedGiftTime = feedGiftTime;
//    }


    public long getDailyTime() {
        return dailyTime;
    }

    public void setDailyTime(long dailyTime) {
        this.dailyTime = dailyTime;
    }


    public List<Integer> getFavoriteIds() {
        return favoriteIds;
    }


    public void setFavoriteIds(List<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
    }
    
}
