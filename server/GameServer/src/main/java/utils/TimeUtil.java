package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.hutool.core.date.DateBetween;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * 时间提供服务
 */
public class TimeUtil {

    public static long SECOND = 1000;
    public static long MINUTE = 60 * SECOND;
    public static long FIVE_MINUTE = 5 * MINUTE;
    public static long ONE_HOUR = 60 * MINUTE;
    public static long SIX_HOUR = 6 * ONE_HOUR;
    public static long ONE_DAY = 24 * ONE_HOUR;
    public static long ONE_WEEK = 7 * ONE_DAY;

    public static int SIX_TIME = 6;
    public static int SECOND_SECOND = 1;
    public static int MINUTE_SECOND = (int) (MINUTE / SECOND);
    public static int SIX_HOUR_SECOND = (int) (SIX_HOUR / SECOND);

    public static long getSeconds() {
        long now = System.currentTimeMillis();
        return now / SECOND;
    }

    public static long getMinute() {
        long now = System.currentTimeMillis();
        return now / MINUTE;
    }

    public static String getDateString(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(c.getTime());

    }

    /**
     * 返回日期格式 20180808
     * 
     * @return
     */
    public static int getToday() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100
                + c.get(Calendar.DATE);
    }

    /**
     * 时间格式转换
     * 
     * @param date
     * @param format - 格式
     * @return {@link String}
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }


    public static long getNextZeroClock() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return getTheZeroClock(date1) == getTheZeroClock(date2);
    }

    public static long getTheZeroClock(Date date) {
        if (date == null)
            return 0;
        // 使用Calendar解决时区等问题
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取昨天是周几
     */
    public static int getYesterdayOfWeek(int today) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, today - 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当天是周几
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取昨天是几号
     */
    public static int getYesterdayOfMonth(int today) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, today - 1);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取给定日期凌晨到给定日期按60进制时分秒经过的秒数
     * 
     * @param now 需要计算的日期
     * @return 获取的秒数
     */
    public static int getSecondsWithTodayHourMinute(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        long millSeconds = c.get(Calendar.HOUR_OF_DAY) * ONE_HOUR;
        millSeconds += c.get(Calendar.MINUTE) * MINUTE;
        millSeconds += c.get(Calendar.SECOND);
        return (int) (millSeconds / SECOND);
    }

    /**
     * 获取给定日期及当日时分秒构造新的date
     * 
     * @param now 需要计算的日期
     * @param hour 需要设置的小时
     * @param minute 需要设置的分钟
     * @return 获取的date
     */
    public static Date getDateWithHourMinute(Date now, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /** 获取当前整点时间 **/
    public static long getTodayOfhonur() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long betweenDay6Clock(Date date1, Date date2) {
        // 当前时间基准线
        Date date = null;
        if (date1.getTime() > date2.getTime()) {
            date = DateEx.getDayHour(date1, 0, SIX_TIME);
        } else {
            date = DateEx.getDayHour(date2, 0, SIX_TIME);
        }
        return DateUtil.between(beginDate(date1, date), beginDate(date2, date), DateUnit.DAY);
    }

    public static Date beginDate(Date date, Date orginDate) {
        if (isSameDay(date, orginDate) && date.getTime() < orginDate.getTime()) {
            return DateEx.getDayHour(date, -1, SIX_TIME);
        } else if (isSameDay(date, orginDate)) {
            return orginDate;
        } else {
            return DateEx.getDayHour(date, 0, SIX_TIME);
        }
    }

    /**
     * 加秒
     */
    public static Date addSecond(Date date, int amount) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, amount);
        return cal.getTime();
    }

    /**
     * 加分钟
     */
    public static Date addMinute(Date date, int amount) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, amount);
        return cal.getTime();
    }
    
    /**
     * 加天
     */
    public static Date addDayOfMonth(Date date, int amount) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, amount);
        return cal.getTime();
    }

    public static int getBetweenTime(int betweenTime, int calendarUnit, DateUnit dateUnit) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.add(calendarUnit, betweenTime);
        DateBetween between = new DateBetween(c1.getTime(), c2.getTime(), true);
        return (int) between.between(dateUnit);
    }

    public static void main(String[] args) {
        // getZeroClock();
        // Date now = new Date();
        // System.err.println(now);
        // System.err.println(addSecond(now, 60));
        System.out.println(getTodayOfhonur());
    }
}
