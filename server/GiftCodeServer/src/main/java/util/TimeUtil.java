package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author wk.dai
 * 
 */
public final class TimeUtil {

	private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat datetimeFormat1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss SSS");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** 毫秒计时 */
	public static final long SECOND = 1000L;
	public static final long FIVE_SECOND = 1000L * 20;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long YEAR = 365 * 24 * HOUR;
	public static final long MONTH = 30 * 24 * HOUR;
	public static final long ABOUT_DAY = DAY - MINUTE * 10;

	@SuppressWarnings("deprecation")
	public static final Date DEFAULT_TIME = new Date("1979/01/01");

	/**
	 * 根据日期返回 yyyy-MM-dd HH:mm:ss SSS 格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static final String getNowDataTimeString1() {
		return datetimeFormat1.format(Calendar.getInstance().getTime());
	}

	/**
	 * 根据日期返回 yyyy-MM-dd HH:mm:ss 格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static final String getNowDataTimeString() {
		return datetimeFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 根据日期返回 yyyy-MM-dd HH:mm:ss 格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static final String getStringByDate(Date date) {
		if (date == null) {
			return "";
		}
		return datetimeFormat.format(date);
	}

	/**
	 * 根据日期返回 yyyy-MM-dd 格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static final String getStringDayByDate(Date date) {
		if (date == null) {
			return "";
		}
		return dateFormat.format(date);
	}

	/**
	 * 判断两个时间是否同一天
	 * 
	 * @param time1
	 *            时间点1
	 * @param time2
	 *            时间点2
	 * @return boolean 是否同一天
	 * 
	 */
	public static boolean isSameDay(Date time1, Date time2) {
		if (time1 == null || time2 == null) {
			return false;
		}

		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(time1);
		calendar2.setTime(time2);
		int day1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int year1 = calendar1.get(Calendar.YEAR);
		int year2 = calendar2.get(Calendar.YEAR);
		return day1 == day2 && year1 == year2;
	}
	
	/**
	 * 判断两个时间中间隔了几天
	 * 
	 * @param date_start
	 *            时间点1
	 * @param date_end
	 *            时间点2
	 * @return int 隔得天数
	 * 
	 */
	
	public static int getDaysBetween(Date date_start, Date date_end) {
			  Calendar d1 = Calendar.getInstance();
			  Calendar d2 = Calendar.getInstance();
			  d1.setTime(date_start);
			  d2.setTime(date_end);
			  if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			   java.util.Calendar swap = d1;
			   d1 = d2;
			   d2 = swap;
			  }
			  int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
			    - d1.get(java.util.Calendar.DAY_OF_YEAR);
			  int y2 = d2.get(java.util.Calendar.YEAR);
			  if (d1.get(java.util.Calendar.YEAR) != y2) {
			   d1 = (java.util.Calendar) d1.clone();
			   do {
			    days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
			    d1.add(java.util.Calendar.YEAR, 1);
			   } while (d1.get(java.util.Calendar.YEAR) != y2);
			  }
			  return days;
			}
	
	/**
	 * 判断两个时间是否同一天
	 * 
	 * @param time1
	 *            时间点1
	 * @param time2
	 *            时间点2
	 * @return boolean 是否同一天
	 */
	public static boolean isSameDay(long time1, long time2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTimeInMillis(time1);
		calendar2.setTimeInMillis(time2);
		int day1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int year1 = calendar1.get(Calendar.YEAR);
		int year2 = calendar2.get(Calendar.YEAR);
		return day1 == day2 && year1 == year2;
	}

	/**
	 * 获取当前时间 （忽略时分秒）
	 * 
	 * @return Date
	 */
	public Date getDateNow() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		calendar.set(year, month, date, 0, 0, 0);
		return calendar.getTime();
	}

	/**
	 * @param timeMillis
	 * @return
	 */
	public static boolean isToday(long timeMillis) {
		return isSameDay(timeMillis, System.currentTimeMillis());
	}

	/**
	 * 判断是否今天
	 * 
	 * @param time
	 *            时间点
	 * @return boolean 是否今天
	 * 
	 */
	public static boolean isToday(Date time) {
		if (time == null) {
			return true;
		}
		return isSameDay(time, new Date());
	}

	/**
	 * 计算两个时间点的间隔(毫秒)
	 * 
	 * @param beforeDate
	 *            前一个时间点
	 * @param afterDate
	 *            前一个时间点
	 * @return long
	 * 
	 */
	public static long getInteval(Date beforeDate, Date afterDate) {
		return afterDate.getTime() - beforeDate.getTime();
	}

	/**
	 * 计算时间点与当前时间的间隔(毫秒)
	 * 
	 * @param beforeDate
	 *            前一个时间点
	 * @return long
	 * 
	 */
	public static long getInteval(Date beforeDate) {
		Date now = new Date();
		return getInteval(beforeDate, now);
	}

	/**
	 * 计算时间点是否处于两个时间点之间
	 * 
	 * @param time
	 *            时间点
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return boolean
	 * 
	 */
	public static boolean containTime(Date time, Date beginTime, Date endTime) {
		if (beginTime != null && time.before(beginTime))
			return false;

		if (endTime != null && time.after(endTime))
			return false;
		return true;
	}

	/**
	 * 计算现在否处于两个时间点之间
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return boolean
	 * 
	 */
	public static boolean containNow(Date beginTime, Date endTime) {
		Date now = new Date();
		return containTime(now, beginTime, endTime);
	}

	/**
	 * 获取两个时间点间隔(毫秒)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return long 毫秒
	 */
	public static long getInterval(Date beginTime, Date endTime) {
		return endTime.getTime() - beginTime.getTime();
	}

	/**
	 * 获取两个时间点间隔(秒)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return float 秒
	 */
	public static float getIntervalSecond(Date beginTime, Date endTime) {
		long millisecond = beginTime.getTime() - beginTime.getTime();
		float second = millisecond / (float) SECOND;
		return second;
	}

	/**
	 * 获取两个时间点间隔(分)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return float 分钟
	 */
	public static float getIntervalMinute(Date beginTime, Date endTime) {
		long millisecond = beginTime.getTime() - beginTime.getTime();
		float minute = millisecond / (float) MINUTE;
		return minute;
	}

	/**
	 * 获取两个时间点间隔(小时)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return float 小时
	 */
	public static float getIntervalHour(Date beginTime, Date endTime) {
		long millisecond = beginTime.getTime() - beginTime.getTime();
		float hour = millisecond / (float) HOUR;
		return hour;
	}

	/**
	 * 获取两个时间点间隔(天)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return float 天
	 */
	public static float getIntervalDay(Date beginTime, Date endTime) {
		long millisecond = beginTime.getTime() - beginTime.getTime();
		float day = millisecond / (float) DAY;
		return day;
	}

	/**
	 * 将字符串日期时间转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param datetime
	 * @return
	 */
	public static Date parseDatetime(String datetime) throws Exception {
		if (datetime == null) {
			return getNowDate();
		}
		return datetimeFormat.parse(datetime);
	}

	public static final Date parse(String expression) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(expression);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获得当天任意时间
	 * 
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static final Date getDate(int hour, int minute, int second) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.MILLISECOND, 0);
		Date date = new Date(cal.getTimeInMillis());
		try {
			return format.parse(format.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param lastEat
	 * @param start
	 * @param end
	 * @return
	 */
	public static final boolean isAlreadyEat(Date lastEat, Date start, Date end) {
		boolean today = isToday(lastEat);
		if (!today)
			return false;

		if (lastEat.after(start) && lastEat.before(end))
			return true;
		return false;
	}

	/**
	 * 获得当前时间时间
	 * 
	 * @return
	 */
	public static final Date getNowDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获得当前时间提早5小时的时间
	 * 
	 * @return
	 */
	public static final Date getBeforeDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		return calendar.getTime();
	}

	/**
	 * 当月第几天(将时间提早5小时)
	 * 
	 * @return
	 */
	public static final int getMonthDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得当前月份
	 * 
	 * @return
	 */
	public static final int getCurrMonth() {
		Calendar calendar = Calendar.getInstance();
		return (int) calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获得时间的月份
	 * 
	 * @return
	 */
	public static final int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date == null ? new Date() : date);
		return (int) calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 取得当月天数
	 * */
	public static final int getCurrentMonthLastDay() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		return a.get(Calendar.DATE);
	}

	// private static final int MAX_MS = 1000 * 60 * 60 * 24 * 7;
	private static final int PERDAY_MS = 1000 * 60 * 60 * 24;

	public static final int getDaysByToday(Date date) {
		if (date == null) {
			return 0;
		} else {
			Calendar compare = Calendar.getInstance();
			compare.setTime(date);

			compare.set(Calendar.HOUR_OF_DAY, 0);
			compare.set(Calendar.MINUTE, 0);
			compare.set(Calendar.SECOND, 0);

			long timeSpace = System.currentTimeMillis()
					- compare.getTimeInMillis();
			if (timeSpace < 0) {
				compare.add(Calendar.DATE, 1);
				timeSpace = System.currentTimeMillis()
						- compare.getTimeInMillis();
				int days = (int) (timeSpace / PERDAY_MS);
				return days;
			} else {
				int days = (int) (timeSpace / PERDAY_MS);
				return days;
			}
		}
	}
	
	public static final int getDaysByYesterday(Date date) {
		if (date == null) {
			return 0;
		} else {
			Calendar compare = Calendar.getInstance();
			compare.setTime(date);

			compare.set(Calendar.HOUR_OF_DAY, 0);
			compare.set(Calendar.MINUTE, 0);
			compare.set(Calendar.SECOND, 0);

			long timeSpace = System.currentTimeMillis()
					- compare.getTimeInMillis();
			if (timeSpace < 0) {
				return 0;
			} else {
				int days = (int) (timeSpace / PERDAY_MS);
				return days + 1;
			}
		}
	}

	// public static final int getDays(Date date){
	// if (date == null) {
	// return 0;
	// } else {
	// Calendar now = Calendar.getInstance();
	// Calendar compare = Calendar.getInstance();
	// compare.setTime(date);
	// if (now.getTimeInMillis() - compare.getTimeInMillis() >= MAX_MS) {
	// return 7;
	// }
	//
	// compare.add(Calendar.DAY_OF_YEAR, 1);
	// compare.set(Calendar.HOUR_OF_DAY, 0);
	// compare.set(Calendar.MINUTE, 0);
	// compare.set(Calendar.SECOND, 0);
	// int days = 1;
	// while (!compare.after(now)) {
	// ++days;
	// compare.add(Calendar.DAY_OF_YEAR, 1);
	// }
	// return days;
	// }
	// }
	
	/**
	 * @param dateTime
	 * @return
	 */
	public static int calculateDateCount(Date dateTime) {
		int dateCount = 0;
		Calendar calendar = Calendar.getInstance();
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int year = calendar.get(Calendar.YEAR);

		Calendar lastLogon = Calendar.getInstance();
		lastLogon.setTime(dateTime);
		int lastLogonDayOfYear = lastLogon.get(Calendar.DAY_OF_YEAR);
		int lastLogonYear = calendar.get(Calendar.YEAR);
		if (year == lastLogonYear) {
			dateCount = dayOfYear - lastLogonDayOfYear;
		} else {
			dateCount = dayOfYear;
			int maxDay = lastLogon.getLeastMaximum(Calendar.DAY_OF_YEAR);
			dateCount += maxDay - lastLogonDayOfYear;
		}
		return dateCount;
	}
	
	/**
	 * 获取后一个时间点是前一个时间点的第几天(天)
	 * 
	 * @param beginTime
	 *            前一个时间点
	 * @param endTime
	 *            后一个时间点
	 * @return float 天
	 */
	public static int getDays(Date beginTime, Date endTime) {
		long millisecond = Math.abs(beginTime.getTime() - endTime.getTime());
		float day = millisecond / (float) DAY;
		return (int) Math.ceil(day);
	}
	
	/**
	 * 获得今天是星期几 周日是一周的第一天
	 * */
	public static int getWeekDay() {
		Calendar compare = Calendar.getInstance();
		compare.setTime(new Date());
		return compare.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 获取下周一10点的时间
	 * */
	public static long getNextMonTen() {
		Calendar compare = Calendar.getInstance();
		compare.setTime(new Date());
		compare.set(Calendar.HOUR_OF_DAY, 10);
		compare.set(Calendar.MINUTE, 0);
		compare.set(Calendar.SECOND, 0);
		compare.set(Calendar.MILLISECOND, 0);
		int day = 0;
		switch (compare.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			day = 1;
			break;
		case 2:
			break;
		case 3:
			day = 6;
			break;
		case 4:
			day = 5;
			break;
		case 5:
			day = 4;
			break;
		case 6:
			day = 3;
			break;
		case 7:
			day = 2;
			break;
		}
		return compare.getTimeInMillis() + DAY * day;
	}

	/**
	 * 获取下一天的日期
	 * @param date
	 * @return
	 */
	public static Date getNextDate(Date date){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.DATE, 1);  
		Date nextDate = calendar.getTime(); 
		return nextDate;
	}
	
	/**
	 * 获取上一天的日期
	 * @param date
	 * @return
	 */
	public static Date getBeforeDate(Date date){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.DATE, -1);  
		Date nextDate = calendar.getTime(); 
		return nextDate;
	}
	
	/**
	 * 获取下个时间
	 * @param date
	 * @param type
	 * @return
	 */
	public static Date getNextDate(Date date, int type, int num){
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(type, num);  
		return calendar.getTime();
	}

	/**
	 * 获取本周某周天时间点
	 * @param date
	 * @param weekDay
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date getDateOfWeekDay(Date date, int weekDay, int hour, int minute, int second){
		return getCalendarOfWeekDay(date, weekDay, hour, minute, second).getTime();
	}
	
	/**
	 * 获取下周某周天时间点
	 * @param date
	 * @param weekDay
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date getDateOfNextWeekDay(Date date, int weekDay, int hour, int minute, int second){
		Calendar calendar = getCalendarOfWeekDay(date, weekDay, hour, minute, second);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		return calendar.getTime();
	}

	private static Calendar getCalendarOfWeekDay(Date date, int weekDay, int hour, int minute, int second){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		weekDay = weekDay % 7 + 1;
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if(day == 1 && weekDay > 1){
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
		}else if(weekDay == 1 && day > 1){
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, weekDay);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static void main(String[] args) {
		//System.out.println(TimeUtil.isSameDay(new Date(), new Date()));
		//System.out.println(getNowDataTimeString());
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy:hh/mm/ss");
		long HOUR_LONG_TIME = 60 * 60 * 1000;
		System.out.println(":"+ new Date(new Date().getTime()
					+ 504 * HOUR_LONG_TIME));
		System.out.println(sdf.format(new Date(new Date().getTime()
				+ 504 * HOUR_LONG_TIME)));
		try {
			Date da = parseDatetime("2017-06-26 00:00:00");
			System.out.println(System.currentTimeMillis()/1000 - da.getTime()/1000 <= (28 * 24 * 60 * 60) - 60);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
