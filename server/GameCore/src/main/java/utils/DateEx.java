package utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import utils.lang.FormatEx;


public class DateEx {
    // 字母 日期或时间元素 表示 示例
    // ------------------------------------------------------------------
    // G Era 标志符 Text AD
    // y 年 Year 1996; 96
    // M 年中的月份 Month July; Jul; 07
    // w 年中的周数 Number 27
    // W 月份中的周数 Number 2
    // D 年中的天数 Number 189
    // d 月份中的天数 Number 10
    // F 月份中的星期 Number 2
    // E 星期中的天数 Text Tuesday; Tue
    // a Am/pm 标记 Text PM
    // H 一天中的小时数（0-23） Number 0
    // k 一天中的小时数（1-24） Number 24
    // K am/pm 中的小时数（0-11） Number 0
    // h am/pm 中的小时数（1-12） Number 12
    // m 小时中的分钟数 Number 30
    // s 分钟中的秒数 Number 55
    // S 毫秒数 Number 978
    // z 时区 General time zone Pacific Standard Time;
    // PST; GMT-08:00
    // Z 时区 RFC 822 time zone -0800

    public static final String fmt_yyyy_MM_W = "yyyy-MM-W";

    public static final String fmt_yyyy_MM_dd_HH_mm_ss_sss = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String fmt_yyyyMMddHHmmsssss = "yyyyMMddHHmmssSSS";

    public static final String fmt_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static final String fmt_num_yyyy_MM_dd_HH_mm_ss = "yyyyMMddHHmmss";

    public static final String fmt_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

    public static final String fmt_yyyy_MM_dd_HH = "yyyy-MM-dd HH";

    public static final String fmt_MM_dd_HH_mm = "MM-dd HH:mm";

    public static final String fmt_yyyy_MM_dd = "yyyy-MM-dd";

    public static final String fmt_yyyy_MM = "yyyy-MM";

    public static final String fmt_MM_dd = "MM-dd";

    public static final String fmt_HH_mm = "HH:mm";

    public static final String fmt_HH_mm_ss = "HH:mm:ss";

    public static final String fmt_yyyy = "yyyy";

    public static final String fmt_MM = "MM";

    public static final String fmt_dd = "dd";

    public static final String fmt_HH = "HH";

    public static final String fmt_mm = "mm";

    public static final String fmt_ss = "ss";

    public static final String fmt_SSS = "SSS";

    public static final long TIME_MILLISECOND = 1;

    public static final long TIME_SECOND = 1000 * TIME_MILLISECOND;

    public static final long TIME_MINUTE = 60 * TIME_SECOND;

    public static final long TIME_HOUR = 60 * TIME_MINUTE;

    public static final long TIME_DAY = 24 * TIME_HOUR;

    public static final long TIME_WEEK = 7 * TIME_DAY;

    public static final long TIME_YEAR = 365 * TIME_DAY;

    public static final long now() {
        return System.currentTimeMillis();
    }

    public static final String now2() {
        return now(fmt_yyyy_MM_dd_HH_mm_ss);
    }

    public static final String now3() {
        return now(fmt_yyyy_MM_dd);
    }

    public static final String now(String fmt) {
        return format(new Date(), fmt);
    }

    public static final Date parseDate(String v, String fmt) throws ParseException {
        Date dat = null;
        dat = FormatEx.parseDate(v, fmt);
        return dat;
    }

    public static final Date parse(String v, String fmt) throws ParseException {
        return parseDate(v, fmt);
    }

    // public static final String formatString(Date d, String fmt) {
    // //SimpleDateFormat myFmt = FormatEx.getFormat(fmt);
    // //return myFmt.format(v);
    //
    // return FormatEx.formatString(d, fmt);
    // }

    public static final String format(Date d, String fmt) {
        // return formatString(v, fmt);
        return FormatEx.formatString(d, fmt);
    }

    public static final int year() {
        return year(new Date());
    }

    public static final int year(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_yyyy);
        // String str = myFmt.format(v);

        String str = FormatEx.formatString(d, fmt_yyyy);
        return NumEx.stringToInt(str);
    }

    public static final int month() {
        return month(new Date());
    }

    public static final int month(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_MM);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_MM);
        return NumEx.stringToInt(str);
    }

    public static final int day() {
        return day(new Date());
    }

    public static final int day(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_dd);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_dd);
        return NumEx.stringToInt(str);
    }

    public static final int hour() {
        return hour(new Date());
    }

    public static final int hour(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_HH);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_HH);
        return NumEx.stringToInt(str);
    }

    public static final int minute() {
        return minute(new Date());
    }

    public static final int minute(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_mm);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_mm);
        return NumEx.stringToInt(str);
    }

    public static final int second() {
        return second(new Date());
    }

    public static final int second(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_ss);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_ss);
        return NumEx.stringToInt(str);
    }

    public static final int ms() {
        return ms(new Date());
    }

    public static final int ms(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat(fmt_SSS);
        // String str = myFmt.format(v);
        // return NumEx.stringToInt(str);

        String str = FormatEx.formatString(d, fmt_SSS);
        return NumEx.stringToInt(str);
    }

    public static final int week() {
        return week(new Date());
    }

    public static final int week(Date v) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(v.getTime());
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        cd = null;
        return dayOfWeek - 1;
    }

    public static final int weekInYear(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat("w");
        // return NumEx.stringToInt(myFmt.format(v));

        String str = FormatEx.formatString(d, "w");
        return NumEx.stringToInt(str);
    }

    public static final int weekInYear() {
        return weekInYear(new Date());
    }

    public static final int weekInMonth(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat("W");
        // return NumEx.stringToInt(myFmt.format(v));

        String str = FormatEx.formatString(d, "W");
        return NumEx.stringToInt(str);
    }

    public static final int weekInMonth() {
        return weekInMonth(new Date());
    }

    public static final int dayInYear(Date d) {
        // SimpleDateFormat myFmt = FormatEx.getFormat("D");
        // return NumEx.stringToInt(myFmt.format(v));

        String str = FormatEx.formatString(d, "D");
        return NumEx.stringToInt(str);
    }

    public static final int dayInYear() {
        return dayInYear(new Date());
    }

    public static final long sub(Date d1, Date d2) {
        long l1 = d1.getTime();
        long l2 = d2.getTime();
        return l1 - l2;
    }

    public static final int dayNum(int year, int month) {
        Calendar test = Calendar.getInstance();
        test.set(Calendar.YEAR, year);
        test.set(Calendar.MONTH, month);
        int totalDay = test.getActualMaximum(Calendar.DAY_OF_MONTH);
        return totalDay;
    }

    public static boolean isTimeout(long lastTm, long timeout) {
        if (timeout <= 0)
            return false;
        long l2 = System.currentTimeMillis();
        long t = l2 - lastTm;
        return (t > timeout);
    }

    public static boolean isTimeout(Date lastDat, long timeout) {
        if (timeout <= 0)
            return false;
        long LASTTIME = lastDat.getTime();
        long l2 = System.currentTimeMillis();
        long t = l2 - LASTTIME;
        return (t > timeout);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                    .get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }


    public static boolean isToday(Date date) {
        return isSameDay(new Date(), date);
    }

    public static boolean isToday(long date) {
        return isSameDay(new Date(), new Date(date));
    }


    public static Date getNextZeroDay(Date date) {
        return getDayHour(date, 1, 0);
    }

    public static Date getNextDayHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    public static Date getDayHour(Date date, int day, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, day);

        return cal.getTime();
    }
    
    public static Date getNextDateMinute(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MINUTE, minute);

        return cal.getTime();
    }

    /**
     * 获取最近的一个周几固定点时间
     * 
     * @param date 参考时间点
     * @param dayOfWeek （1-7对应周一至周日）
     * @param hour 时（24小时制）
     * @return
     */
    public static Date getNextWeekHour(Date date, int dayOfWeek, int hour) {
        long time = date.getTime();
        int day = dayOfWeek + 1;
        day = day % 7;// DAY_OF_WEEK转换
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() < time) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
        }
        return cal.getTime();
    }

    /**
     * 获取最近的一个月1号固定点时间
     * 
     * @param date 参考时间点
     * @param hour 时（24小时制）
     * @return
     */
    public static Date getNextMonthFirstDayHour(Date date, int hour) {
        long time = date.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() < time) {
            cal.add(Calendar.MONTH, 1);
        }
        return cal.getTime();
    }

    /**
     * 获取指定日期
     * 
     * @param time
     * @return
     */
    public static Calendar getAssignCalendar(int[] time) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, time[0]);
        c.set(Calendar.MONTH, time[1]);
        c.set(Calendar.DAY_OF_MONTH, time[2]);
        if (time.length > 3) {
            c.set(Calendar.HOUR_OF_DAY, time[3]);
            c.set(Calendar.MINUTE, time[4]);
            c.set(Calendar.SECOND, time[5]);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        }
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取指定日期
     * 
     * @param time
     * @return
     */
    public static Date getAssignDate(int[] time) {
        return getAssignCalendar(time).getTime();
    }

    public static final String toString(long ms) {// 将毫秒数换算成x天x时x分x秒x毫秒
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day;
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " " + strMilliSecond;
    }
}
