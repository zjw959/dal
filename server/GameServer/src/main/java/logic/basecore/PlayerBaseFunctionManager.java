package logic.basecore;

import java.util.Date;

import logic.character.bean.Player;
import utils.DateEx;
import utils.ToolMap;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

/**
 * 玩家功能Manager基类
 */
public abstract class PlayerBaseFunctionManager {
    protected transient Player player;

    /**
     * 系统下次跨天时间(0点)
     * 
     * 避免注册新号就跨天
     * 
     * arocssSysDay
     */
    protected long aSysDay = getSysNextAcrossDay(new Date());

    /**
     * 游戏下次跨天时间(默认 6点)
     * 
     * 避免注册新号就跨天
     * 
     * arocssGameDay
     */
    protected long aGameDay = getGameNextAcrossDay(new Date());

    /**
     * 系统下次跨周时间(周一0点)
     * 
     * 避免注册新号就跨周
     * 
     * arocssSysWeek
     */
    protected long aSysWeek = getSysNextAcrossWeek(new Date());

    /**
     * 游戏下次跨周时间(默认周一 6点)
     * 
     * 避免注册新号就跨周
     * 
     * arocssGameWeek
     */
    protected long aGameWeek = getGameNextAcrossWeek(new Date());
    /**
     * 系统下次跨月时间(周一0点)
     * 
     * 避免注册新号就跨月
     * 
     * arocssSysWeek
     */
    protected long aSysMonth = getSysNextAcrossMonth(new Date());

    /**
     * 游戏下次跨月时间(默认1号 6点)
     * 
     * 避免注册新号就跨月
     * 
     * arocssGameWeek
     */
    protected long aGameMonth = getGameNextAcrossMonth(new Date());

    public String getKey() {
        return this.getClass().getSimpleName();
    }

    protected Player getPlayer() {
        return player;
    }

    public long getSysAcrossDay() {
        return this.aSysDay;
    }

    public long getGameAcrossDay() {
        return this.aGameDay;
    }

    public void setSysAcrossDay(long currentTimeMillis) {
        this.aSysDay = currentTimeMillis;
    }

    public void setGameAcrossDay(long currentTimeMillis) {
        this.aGameDay = currentTimeMillis;
    }

    public long getSysNextAcrossDay(Date date) {
        return DateEx.getNextZeroDay(date).getTime();
    }

    public long getGameNextAcrossDay(Date date) {
        int hour = 6;
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(15001);
        if (bean != null) {
            hour = ToolMap.getInt("time", bean.getData(), 6);
        }
        return DateEx.getNextDayHour(date, hour).getTime();
    }

    /** 获取系统下一个跨周时间点（周一0点） */
    public long getSysNextAcrossWeek(Date date) {
        return DateEx.getNextWeekHour(date, 1, 0).getTime();
    }

    /** 获取游戏内下一个跨周时间点（周一6点） */
    public long getGameNextAcrossWeek(Date date) {
        int hour = 6;
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(15001);
        if (bean != null) {
            hour = ToolMap.getInt("time", bean.getData(), 6);
        }
        return DateEx.getNextWeekHour(date, 1, hour).getTime();
    }

    /** 获取系统下一个跨月时间点（1号0点） */
    public long getSysNextAcrossMonth(Date date) {
        return DateEx.getNextMonthFirstDayHour(date, 0).getTime();
    }

    /** 获取游戏内下一个跨月时间点（1号6点） */
    public long getGameNextAcrossMonth(Date date) {
        int hour = 6;
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(15001);
        if (bean != null) {
            hour = ToolMap.getInt("time", bean.getData(), 6);
        }
        return DateEx.getNextMonthFirstDayHour(date, hour).getTime();
    }

    public long getSysAcrossWeek() {
        return this.aSysWeek;
    }

    public long getGameAcrossWeek() {
        return this.aGameWeek;
    }

    public void setSysAcrossWeek(long time) {
        this.aSysWeek = time;
    }

    public void setGameAcrossWeek(long time) {
        this.aGameWeek = time;
    }

    public long getSysAcrossMonth() {
        return this.aSysMonth;
    }

    public long getGameAcrossMonth() {
        return this.aGameMonth;
    }

    public void setSysAcrossMonth(long time) {
        this.aSysMonth = time;
    }

    public void setGameAcrossMonth(long time) {
        this.aGameMonth = time;
    }

    // protected PlayerBaseFunctionManager(Player player) {
    // this.player = player;
    // }

    public static void main(String[] args) {
        String d =
                DateEx.format(DateEx.getNextMonthFirstDayHour(new Date(), 19),
                        DateEx.fmt_yyyy_MM_dd_HH_mm_ss);
        System.out.println("----t=" + d);
    }
}
