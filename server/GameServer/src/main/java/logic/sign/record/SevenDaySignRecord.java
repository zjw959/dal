package logic.sign.record;

import utils.TimeUtil;

/***
 * 
 * 玩家N天签到奖励记录器
 * 
 * @author lihongji
 *
 */
public class SevenDaySignRecord {

    /****
     * 领取的位置
     */
    private int index;

    /** 预留位置 **/
    private int preIndex;
    /***
     * 当前领奖时间
     */
    private long loginTime;

    /** 结束时间 **/
    private long endTime;

    /** 活动指定id **/
    private int activity_id;

    /** 记录领取奖励信息 **/
    public void addAchieveRecord(boolean flag) {
        index += preIndex;
        preIndex = 0;
        if (flag)
            endTime = TimeUtil.getNextZeroClock();
    }

    /** 重置记录 **/
    public void reset() {
        index = 0;
        preIndex = 0;
        loginTime = 0;
        endTime = 0;
    }


    /** 增加玩家可领取信息 **/
    public void addMayAchieveRecord() {
        preIndex += 1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPreIndex() {
        return preIndex;
    }

    public void setPreIndex(int preIndex) {
        this.preIndex = preIndex;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

}
