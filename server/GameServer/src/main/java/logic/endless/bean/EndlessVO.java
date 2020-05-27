package logic.endless.bean;

/** 无尽回廊 */
public class EndlessVO {
    private int nowStage = 0;
    /** 最近通关时间 */
    private long lastPassTime = 0;
    /** 最近领奖时间 */
    private long lastRewardTime = 0;
    /** 今日挑战时间 */
    private int todayBest = 0;
    /** 挑战总时间 */
    private int todayCostTime = 0;
    /** 离散最好成绩 */
    private int historyBest = 0;
    /** 当前记录属于第几个周期 */
    private long voCircles;
    /** 阶段过期后可以对最后一次的挑战进行处理 */
    private boolean timeUpSave;


    public boolean isTimeUpSave() {
        return timeUpSave;
    }

    public void setTimeUpSave(boolean timeUpSave) {
        this.timeUpSave = timeUpSave;
    }

    public long getLastPassTime() {
        return lastPassTime;
    }

    public void setLastPassTime(long lastPassTime) {
        this.lastPassTime = lastPassTime;
    }

    public long getLastRewardTime() {
        return lastRewardTime;
    }

    public void setLastRewardTime(long lastRewardTime) {
        this.lastRewardTime = lastRewardTime;
    }

    public int getTodayBest() {
        return todayBest;
    }

    public void setTodayBest(int todayBest) {
        this.todayBest = todayBest;
    }

    public int getTodayCostTime() {
        return todayCostTime;
    }

    public void setTodayCostTime(int todayCostTime) {
        this.todayCostTime = todayCostTime;
    }

    public int getHistoryBest() {
        return historyBest;
    }

    public void setHistoryBest(int historyBest) {
        this.historyBest = historyBest;
    }

    public int getNowStage() {
        return nowStage;
    }

    public void setNowStage(int nowStage) {
        this.nowStage = nowStage;
    }

    public long getVoCircles() {
        return voCircles;
    }

    public void setVoCircles(long voCircles) {
        this.voCircles = voCircles;
    }
}
