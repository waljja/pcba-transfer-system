package com.ht.util;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static Date addDay(Date date,int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_YEAR,(calendar.get(Calendar.DAY_OF_YEAR)+day));
		return calendar.getTime();
	}

	public static Date addMinue(Date date,int minue) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE,(calendar.get(Calendar.MINUTE)+minue));
		return calendar.getTime();
	}

	public static Date addHour(Date date,int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR,(calendar.get(Calendar.HOUR)+hour));
		return calendar.getTime();
	}
	
	/**
	 * 将日期字符串按给定格式转成日期对象
	 * 
	 * @param Time
	 * @param Format
	 *            example:"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static Date getDateFromString(String Time, String Format) {
		Date date = null;

		// Check the proper string length
		if ((Time == null) || (Format == null)) {
			return date;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(Format);

		try {
			date = formatter.parse(Time);
		} catch (Exception e) {
			date = null;
		}

		return date;
	}

	/**
	 * 取得当前月的总天数
	 * 
	 * @param currentDate
	 *            当前时间
	 * @return daysOfmonth
	 */
	public static int getDaysOfMonth(Date currentDate) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		cc.set(Calendar.DATE, 1);
		Date dateStart = cc.getTime();
		cc.add(Calendar.MONTH, 1);
		Date dateEnd = cc.getTime();
		return diffDays(dateStart, dateEnd);
	}

	/**
	 * 将日期按给定格式转成字符串
	 * 
	 * @param date
	 * @param Format
	 *            example:"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getFormattedDate(Date date, String Format) {
		String sTime = null;

		if (date == null || Format == null) {
			return null;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(Format);
		sTime = formatter.format(date);

		return sTime;
	}

	/**
	 * 比较两个时间的大小
	 * 
	 * @param cmpDate
	 *            需要比较的时间
	 * @param amdDate
	 *            参考比较的时间
	 * @return [cmpDate > amdDate : 1][cmpDate = amdDate : 0][cmpDate < amdDate
	 *         : -1]
	 */
	public static int compareDate(Date cmpDate, Date amdDate) {
		Calendar cmpCldar = Calendar.getInstance();
		Calendar amdCldar = Calendar.getInstance();
		cmpCldar.setTime(cmpDate);
		amdCldar.setTime(amdDate);
		return cmpCldar.compareTo(amdCldar);
	}

	public static String formatTime(String time, String preFormat,
			String aftFormat) {
		String sTime = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(preFormat);
			ParsePosition pos = new ParsePosition(0);
			java.util.Date date = formatter.parse(time, pos);
			formatter = new SimpleDateFormat(aftFormat);
			sTime = formatter.format(date);
		} catch (Exception e) {
			sTime = "err";
		}

		return sTime;
	}

	/**
	 * 判断两个日期相差多少天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int diffDays(Date date1, Date date2) {
		int difDays = 0;
		try {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			if (date1.after(date2)) {
				cal1.setTime(date2);
				cal2.setTime(date1);
			} else {
				cal1.setTime(date1);
				cal2.setTime(date2);
			}
			int year1 = cal1.get(Calendar.YEAR);
			int year2 = cal2.get(Calendar.YEAR);
			int d1 = cal1.get(Calendar.DAY_OF_YEAR);
			int d2 = cal2.get(Calendar.DAY_OF_YEAR);

			int difyear = year2 - year1;
			if (difyear < 0) {
				difyear = -difyear;
			}

			if (difyear == 0) {
				difDays = d2 - d1;
			} else {
				difDays = 365 * difyear + d2 - d1;
			}
			return difDays;
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 判断两个日期包含多少天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int hasDays(Date date1, Date date2) {
		return diffDays(date1, date2) + 1;
	}

	/**
	 * 计算年龄
	 * 当前年份 － 员工出生年份 ＋ 0/-1  
 	 *	0:如果生日日期小于当前日期   
 	 *  -1: 如果生日日期大于当前日期
	 * 
	 * @param birthDay
	 * @return age(int)
	 */
	public static int getAge(Date birthDay) {
		if (birthDay == null) {
			return 0;
		}
		int age = 0;
		Calendar currentCalendar = Calendar.getInstance();
		int currentYear = currentCalendar.get(Calendar.YEAR);
		
		Calendar birthCalendar = Calendar.getInstance();
		birthCalendar.setTime(birthDay);
		int birthYear = birthCalendar.get(Calendar.YEAR);
		
		age = currentYear - birthYear;
		int currentMonth =  currentCalendar.get(Calendar.MONTH);
		int birthMonth = birthCalendar.get(Calendar.MONTH);
		if(birthMonth < currentMonth)
		{
			return age;
		}
		else if (birthMonth > currentMonth)
		{
			return age -1;
		}
		else
		{
			int currentDayOfTheMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
			int birthDayOfTheMonth = birthCalendar.get(Calendar.DAY_OF_MONTH);
			
			if(birthDayOfTheMonth <= currentDayOfTheMonth)
			{
				return age;
			}
			else
			{
				return age -1;
			}	
		}
	}
	
	/**
	 * 获取当前时间N秒前的时间
	 * 
	 * @param second
	 * @return
	 */
	public static Date getTimeBefore(long second) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		long mill = calendar.getTimeInMillis();
		calendar.setTimeInMillis(mill - second * 1000);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 获取当前日期的N天前的日期
	 * 
	 * @param daybefore
	 * @param format
	 * @return
	 */
	public static String getDateBefore(int daybefore, String format) {
		String date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -daybefore);
		date = getFormattedDate(calendar.getTime(), format);
		return date;
	}

	/**
	 * 获取目标日期的N天后的日期
	 * 
	 * @param dayafter
	 * @param format
	 * @return
	 */
	public static String getSrcDateAfter(int dayafter, Date srcDate,
			String format) {
		String date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcDate);
		calendar.add(Calendar.DATE, dayafter);
		date = getFormattedDate(calendar.getTime(), format);
		return date;
	}

	/**
	 * 返回当前日期的N月前的日期
	 * 
	 * @param monthbefore
	 * @param format
	 * @return
	 */
	public static String getMonthBefore(int monthbefore, String format) {
		String date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -monthbefore);
		date = getFormattedDate(calendar.getTime(), format);
		return date;
	}

	/**
	 * 返回指定日期的N月前的日期
	 * 
	 * @param currentDate
	 * @param monthbefore
	 * @param format
	 * @return
	 */
	public static String getMonthBefore(Date currentDate, int monthbefore,
			String format) {
		String date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -monthbefore);
		date = getFormattedDate(calendar.getTime(), format);
		return date;
	}

	/**
	 * 取得月未时间(23:59:59)
	 * 
	 * @param currentDate
	 * @return
	 */
	public static Date getMonthLastDate(Date currentDate) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH);
		int days = getDaysOfMonth(currentDate);
		// cc.set(Calendar.DATE, days);
		cc.set(year, month, days, 23, 59, 59);
		return cc.getTime();
	}

	/**
	 * 取得月初时间(1号零时零分零秒)
	 * 
	 * @param currentDate
	 * @return
	 */
	public static Date getMonthFirstDate(Date currentDate) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		// cc.set(Calendar.DATE, 1);
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH);

		// 设置开始时间
		cc.set(year, month, 1, 0, 0, 0);
		return cc.getTime();
	}

	/**
	 * 取得给定日期前或后n个月的月初时间(1号零时零分零秒)
	 * 
	 * @param currentDate
	 * @param n
	 *            值说明：n=-1表示给定日期的上一个月1号，n=-3表示三个月前的1号
	 *            n=1表示给定日期的下一个月，n=2表示两月后的日期1号 n=0表示给定日期的1号
	 * @return
	 */
	public static Date getMonthFirstDate(Date currentDate, int n) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		cc.add(Calendar.MONTH, n);
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH);

		// 设置开始时间
		cc.set(year, month, 1, 0, 0, 0);
		return cc.getTime();
	}

	/**
	 * 返回当前时间所属周的周几时间
	 * 
	 * @param currentDate
	 *            当前时间
	 * @param weekday
	 *            周几[Calendar.weekday]
	 * @return
	 */
	public static Date getWeekendDate(Date currentDate, int weekday) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int w = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, weekday - w);
		return c.getTime();
	}

	/**
	 * 取得当前语言对应的当前时间
	 * 
	 * @return
	 */
	public static Date getNowDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * 取得日期内的元素:年,月,日
	 * 
	 * @param date
	 *            日期
	 * @param type
	 *            Calendar.Year,month,day
	 * @return
	 */
	public static int getDateElements(Date date, int type) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(date);
		return cc.get(type);
	}

	/**
	 * 
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static Date getDate(int year, int month, int date) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, date);
		return c.getTime();

	}

	/**
	 * 实例一个时间类型
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date getDate(int year, int month, int date, int hour,
			int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, hour, minute, second);
		return c.getTime();

	}

	/**
	 * 根据日期获取年
	 * 
	 * @param date
	 * @return
	 */
	public static int getYearByDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 根据日期获取月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthByDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	/**
	 * 根据日期获取日
	 * 
	 * @param date
	 * @return
	 */
	public static int getDateByDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DATE);
	}

	/**
	 * 返回当前日期下的某个时间日期对象
	 * 
	 * @param h
	 * @param m
	 * @param s
	 * @return
	 */
	public static Date getNowDateByTime(int h, int m, int s) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, h);
		calendar.set(Calendar.MINUTE, h);
		calendar.set(Calendar.SECOND, h);
		return calendar.getTime();
	}

	/**
	 * 只比较月和日的大小
	 * 
	 * @param cmpDate
	 *            -需要比较的时间
	 * @param amdDate
	 *            - 参考比较的时间
	 * @return > 1; < -1; == 0;
	 */
	public static int compareMonthAndDay(Date cmpDate, Date amdDate) {
		Calendar cmpCldar = Calendar.getInstance();
		Calendar amdCldar = Calendar.getInstance();

		cmpCldar.setTime(cmpDate);
		amdCldar.setTime(amdDate);

		int DValueMonth = cmpCldar.get(Calendar.MONTH)
				- amdCldar.get(Calendar.MONTH);
		if (DValueMonth > 0) {
			return 1;
		} else if (DValueMonth < 0) {
			return -1;
		} else {
			int DValueDay = cmpCldar.get(Calendar.DAY_OF_MONTH)
					- amdCldar.get(Calendar.DAY_OF_MONTH);
			if (DValueDay > 0) {
				return 1;
			} else if (DValueDay == 0) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	/**
	 * 获取开始时间
	 * @param currentDate
	 * @return
	 */
	public static Date getDayBegin(Date currentDate) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH);
		int day = cc.get(Calendar.DAY_OF_MONTH);

		cc.set(year, month, day, 0, 0, 0);
		return cc.getTime();
	}

	/**
	 * 获取结束时间
	 * @param currentDate
	 * @return
	 */
	public static Date getDayEnd(Date currentDate) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(currentDate);
		
		int year = cc.get(Calendar.YEAR);
		int month = cc.get(Calendar.MONTH);
		int day = cc.get(Calendar.DAY_OF_MONTH);

		cc.set(year, month, day, 23, 59, 59);
		return cc.getTime();
	}
	
}
