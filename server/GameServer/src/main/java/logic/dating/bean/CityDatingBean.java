package logic.dating.bean;

import java.util.HashMap;
import java.util.List;

/**
 * 城市约会对象
 * <p>
 * 对特殊需求的扩展
 * <p>
 * 城市约会对象为触发时创建,创建时不进行约会节点的初始化,使用玩家开始使用约会时的数据进行初始化
 * 
 * @author Alan
 *
 */
public class CityDatingBean extends CurrentDatingBean {

    public CityDatingBean(long id, int datingType, int score, int favorReward, int currentCid,
            int scriptId, List<Integer> roleIds, int buildingCid, long datingBeginTime,
            long datingEndTime, long triggerTime, List<Integer> datingTimeFrame, int state,
            long createTime) {
        super(id, datingType, score, favorReward, currentCid, new HashMap<Integer, List<Integer>>(), scriptId, roleIds);
        this.buildingCid = buildingCid;
        this.datingBeginTime = datingBeginTime;
        this.datingEndTime = datingEndTime;
        this.triggerTime = triggerTime;
        this.datingTimeFrame = datingTimeFrame;
        this.state = state;
        this.createTime = createTime;
    }

    /**
     * 建筑id
     */
    private int buildingCid;

    /**
     * 约会开始时间点
     */
    long datingBeginTime;
    /**
     * 约会结束时间点
     */
    long datingEndTime;
    /**
     * 约会触发时间
     */
    long triggerTime;
    /**
     * 约会配置时段
     */
    List<Integer> datingTimeFrame;
    /**
     * 约会记录的状态
     */
    int state;
    /**
     * 创建时间
     */
    long createTime;
    /**
     * 玩家开始时间点,用于约会正在操作但到达过期时间的特定检测
     */
    transient long playerBeginTime;

    /**
     * 城市id
     */
    public int getBuildingCid() {
        return buildingCid;
    }

    /**
     * 城市id
     */
    public CityDatingBean setBuildingCid(int buildingCid) {
        this.buildingCid = buildingCid;
        return this;
    }

    /**
     * 约会开始时间点
     */
    public long getDatingBeginTime() {
        return datingBeginTime;
    }

    /**
     * 约会开始时间点
     */
    public void setDatingBeginTime(long datingBeginTime) {
        this.datingBeginTime = datingBeginTime;
    }

    /**
     * 约会结束时间点
     */
    public long getDatingEndTime() {
        return datingEndTime;
    }

    /**
     * 约会结束时间点
     */
    public void setDatingEndTime(long datingEndTime) {
        this.datingEndTime = datingEndTime;
    }

    /**
     * 约会触发时间
     */
    public long getTriggerTime() {
        return triggerTime;
    }

    /**
     * 约会触发时间
     */
    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    /**
     * 约会配置时段
     */
    public List<Integer> getDatingTimeFrame() {
        return datingTimeFrame;
    }

    /**
     * 约会配置时段
     */
    public void setDatingTimeFrame(List<Integer> datingTimeFrame) {
        this.datingTimeFrame = datingTimeFrame;
    }

    /**
     * 约会记录的状态
     */
    public int getState() {
        return state;
    }

    /**
     * 约会记录的状态
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 创建时间
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 玩家开始时间点,用于约会正在操作但到达过期时间的特定检测
     */
    public long getPlayerBeginTime() {
        return playerBeginTime;
    }

    /**
     * 玩家开始时间点,用于约会正在操作但到达过期时间的特定检测
     */
    public void setPlayerBeginTime(long playerBeginTime) {
        this.playerBeginTime = playerBeginTime;
    }

}
