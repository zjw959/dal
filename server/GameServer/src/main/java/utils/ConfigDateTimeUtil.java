package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

/**
 * 时间段检查器
 * Created by fxf on 2017/11/23.
 */
public class ConfigDateTimeUtil {

    /**每日固定时间段*/
    private static final int TYPE_DAILY_TIME_FRAME = 2;
    /**每周固定时间段*/
    private static final int TYPE_DAY_OF_WEEK_TIME_FRAME = 3;
    /**周整天开启*/
    private static final int TYPE_ALL_DAY_OF_WEEK = 4;
    /**月整天开启*/
    private static final int TYPE_ALL_DAY_OF_MONTH = 5;
    /**月时间段开启 */
    private static final int TYPE_DAY_OF_MONTH_TIME_FRAME = 6;
    /**指定具体时间段 */
    private static final int TYPE_SPECIFY_TIME_FRAME = 7;

    /** 时间点：开启 */
    public static int TIME_POINT_BEGIN = 0;
    /** 时间点：关闭 */
    public static int TIME_POINT_END = 1;
    /** 时间点：非开启关闭 */
    public static int TIME_POINT_NONE = -1;

    /** 适配游戏跨天配置 */
    public static Date shiftGameAcrossDay(Date date) {
        int hour = 6;
        DiscreteDataCfgBean bean = GameDataManager.getDiscreteDataCfgBean(15001);
        if (bean != null) {
            hour = ToolMap.getInt("time", bean.getData(), 6);
        }
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.HOUR_OF_DAY, -hour);
        return now.getTime();
    }
    
    public static boolean check(int openTimeType, List<Integer> timePoints){
        Date nowDate = new Date();
        return check(openTimeType, timePoints, nowDate);
    }

    /**
     * 检查是否在时间段内
     * <p>0点跨天兼容至动态配置跨天的变换,传入值须为未做偏移的时间
     */
    public static boolean check(int openTimeType, List<Integer> timePoints, Date normalDate) {
        Date date = shiftGameAcrossDay(normalDate);
        int minute;
        int day;
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        boolean result = false;
        switch (openTimeType) {
            case TYPE_DAILY_TIME_FRAME:
                minute = (int) DateUtil.between(DateUtil.beginOfDay(date), date, DateUnit.MINUTE);
                result = isInTimeFrame(timePoints, minute);
                break;
            case TYPE_DAY_OF_WEEK_TIME_FRAME:
                minute = (int) DateUtil.between(DateUtil.beginOfWeek(date), date, DateUnit.MINUTE);
                result = isInTimeFrame(timePoints, minute);
                break;
            case TYPE_ALL_DAY_OF_WEEK:
                day = now.get(Calendar.DAY_OF_WEEK);
                result = timePoints.contains(day);
                break;
            case TYPE_ALL_DAY_OF_MONTH:
                day = now.get(Calendar.DAY_OF_MONTH);
                result = timePoints.contains(day);
                break;
            case TYPE_DAY_OF_MONTH_TIME_FRAME:
                minute = (int) DateUtil.between(DateUtil.beginOfMonth(date), date, DateUnit.MINUTE);
                result = isInTimeFrame(timePoints, minute);
                break;
            case TYPE_SPECIFY_TIME_FRAME:
                Calendar begin = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                Calendar[]calendars = new Calendar[]{begin,end};
                int index = 0;
                for (int i = 0; i < calendars.length; i++) {
                    Calendar c = calendars[i];
                    c.set(Calendar.YEAR, (Integer) timePoints.get(index++));
                    c.set(Calendar.MONTH, (Integer) timePoints.get(index++));
                    c.set(Calendar.DAY_OF_MONTH, (Integer) timePoints.get(index++));
                    c.set(Calendar.HOUR, (Integer) timePoints.get(index++));
                    c.set(Calendar.MINUTE, (Integer) timePoints.get(index++));
                }
                if (begin.getTimeInMillis()<now.getTimeInMillis()&&end.getTimeInMillis()>now.getTimeInMillis() ){
                    result = true;
                }
                break;
            default:
                result = true;
        }
        return result;
    }

    /**
     * 检查是否在时间段范围内
     */
    public static boolean check(List<Integer> openTime,int minute){
        return isInTimeFrame(openTime,minute);
    }

    /**
     * 获取时间段
     * @param timeFrame
     * @param date
     * @return
     */
    public static int getTimePoint(int openTimeType, List<Integer> timePoints, Date lastCheck, Date date) {
        int minute;
        int result;
        int day;
        int lastMinute;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (openTimeType) {
            case TYPE_DAILY_TIME_FRAME:
            	lastMinute = (int) DateUtil.between(DateUtil.beginOfDay(date), lastCheck, DateUnit.MINUTE, false);
                minute = (int) DateUtil.between(DateUtil.beginOfDay(date), date, DateUnit.MINUTE);
                result = getTimePoint(timePoints, lastMinute, minute);
                break;
            case TYPE_DAY_OF_WEEK_TIME_FRAME:
            	lastMinute = (int) DateUtil.between(DateUtil.beginOfDay(date), lastCheck, DateUnit.MINUTE, false);
                minute = (int) DateUtil.between(DateUtil.beginOfWeek(date), date, DateUnit.MINUTE);
                result = getTimePoint(timePoints, lastMinute, minute);
                break;
            case TYPE_ALL_DAY_OF_WEEK:
                if (TimeUtil.isSameDay(lastCheck, date)) {
                	result = TIME_POINT_NONE;
                    break;
                }
                day = calendar.get(Calendar.DAY_OF_WEEK);
                if (timePoints.contains(day) && !timePoints.contains(TimeUtil.getYesterdayOfWeek(day))) {
                    //包含今天不包含昨天：开始时间点
                    result = TIME_POINT_BEGIN;
                } else if (timePoints.contains(TimeUtil.getYesterdayOfWeek(day)) && !timePoints.contains(day)) {
                    //包含昨天不包含今天：结束时间点
                    result = TIME_POINT_END;
                } else {
                    result = TIME_POINT_NONE;
                }
                break;
            case TYPE_ALL_DAY_OF_MONTH:
                if (TimeUtil.isSameDay(lastCheck, date)){
                	result = TIME_POINT_NONE;
                    break;
                }
                day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                if (timePoints.contains(day) && !timePoints.contains(TimeUtil.getYesterdayOfMonth(day))) {
                    //包含今天不包含昨天：开始时间点
                    result = TIME_POINT_BEGIN;
                } else if (timePoints.contains(TimeUtil.getYesterdayOfMonth(day)) && !timePoints.contains(day)) {
                    //包含昨天不包含今天：结束时间点
                    result = TIME_POINT_END;
                } else {
                    result = TIME_POINT_NONE;
                }
                break;
            case TYPE_DAY_OF_MONTH_TIME_FRAME:
            	lastMinute = (int) DateUtil.between(DateUtil.beginOfDay(date), lastCheck, DateUnit.MINUTE, false);
                minute = (int) DateUtil.between(DateUtil.beginOfMonth(date), date, DateUnit.MINUTE);
                result = getTimePoint(timePoints, lastMinute, minute);
                break;
            case TYPE_SPECIFY_TIME_FRAME:
                int index = 0;
                for (int i = 0; i < 2; i++) {
                    if (calendar.get(Calendar.YEAR) == (int) timePoints.get(index++)
                            && calendar.get(Calendar.MONTH) == (int) timePoints.get(index++)
                            && calendar.get(Calendar.DAY_OF_MONTH) == (int) timePoints.get(index++)
                            && calendar.get(Calendar.HOUR) == (int) timePoints.get(index++)
                            && calendar.get(Calendar.MINUTE) == (int) timePoints.get(index++)
                            ) {
                        if (i == 0) {
                            result = TIME_POINT_BEGIN;
                        } else {
                            result = TIME_POINT_END;
                        }
                    }
                }
                result = TIME_POINT_NONE;
                break;
            default:
                result = TIME_POINT_NONE;
        }
        return result;
    }

    /**
     * 是否在时间段范围内
     */
    private static boolean isInTimeFrame(List<Integer> openTime, int minute) {
        for (int i = 0; i < openTime.size(); i = +2) {
            int b = openTime.get(i);
            int e = openTime.get(i + 1);
            if (minute >= b && minute <= e) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据分钟数获取时间点
     */
    private static int getTimePoint(List<Integer> openTime, int lastMinute, int minute){
        int begin,end;
        for (int i = 0; i < openTime.size();) {
            begin = openTime.get(i++);
            end = openTime.get(i++);
            if(lastMinute >= begin && lastMinute <= end)
            	return TIME_POINT_NONE;
            if (begin >= minute){
                return TIME_POINT_BEGIN;
            }
            if (end <= minute){
                return TIME_POINT_END;
            }
        }
        return TIME_POINT_NONE;
    }

}
