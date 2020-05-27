package logic.role.bean;

import java.util.Date;

public class RoleTouch {
    private int tTimes;

    private Date lastRTime;

    public int getTouchTimes() {
        return tTimes;
    }

    public void setTouchTimes(int touchTimes) {
        this.tTimes = touchTimes;
    }

    public Date getLastRecoverTime() {
        return lastRTime;
    }

    public void setLastRecoverTime(Date lastRecoverTime) {
        this.lastRTime = lastRecoverTime;
    }

    public void change(int number) {
        this.tTimes += number;
        if (this.tTimes < 0) {
            this.tTimes = 0;
        }
    }

    public RoleTouch() {}

    public RoleTouch(int touchTimes, Date lastRecoverTime) {
        super();
        this.tTimes = touchTimes;
        this.lastRTime = lastRecoverTime;
    }

}
