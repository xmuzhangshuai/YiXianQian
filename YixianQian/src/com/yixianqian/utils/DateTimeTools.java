package com.yixianqian.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**   
*    
* 项目名称：ExamHelper   
* 类名称：DateTimeTools   
* 类描述：   获取时间日期的工具类
* 创建人：张帅  
* 创建时间：2014-1-4 上午10:28:56   
* 修改人：张帅   
* 修改时间：2014-1-4 上午10:28:56   
* 修改备注：   
* @version    
*    
*/
public class DateTimeTools {
	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");

	public DateTimeTools() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 返回当前系统时间
	 * @return Date格式
	 */
	public static Date getCurrentDate() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return curDate;
	}

	/**
	 * 比较和当前时间差，返回String
	 * @param date
	 * @return
	 */
	public static String getInterval(Date date) {
		String intervalString = "";
		// 获取时间差
		Map<String, Integer> interval = DateTimeTools.compareTo(DateTimeTools.getCurrentDate(), date);
		// 根据时间先后设置人性化时间提醒
		if (interval.get("year") >= 1) {
			intervalString = interval.get("year") + "年前";
		} else if (interval.get("month") >= 1) {
			intervalString = interval.get("month") + "月前";
		} else if (interval.get("day") >= 1) {
			intervalString = interval.get("day") + "天前";
		} else if (interval.get("hour") >= 1) {
			intervalString = interval.get("hour") + "小时前";
		} else if (interval.get("minute") >= 3) {
			intervalString = interval.get("minute") + "分钟前";
		} else {
			intervalString = "刚刚";
		}
		return intervalString;
	}

	/**
	 * 返回当前系统时间
	 * @return String格式
	 */
	public static String getCurrentDateForString() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	/**
	 * 将日期格式转化为字符串返回
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date) {
		return formatter.format(date);
	}

	/**
	 * 计算日期时间之间的差值， date1得时间必须大于date2的时间
	 * @param date1 
	 * @param date2
	 * @return {@link java.util.Map} Map的键分别为, day(天), hour(小时),minute(分钟)和second(秒)。
	 */
	public static Map<String, Long> timeDifference(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			throw new NullPointerException("date1 and date2 can't null");
		}
		long mim1 = date1.getTime();
		long mim2 = date2.getTime();
		if (mim1 < mim2) {
			throw new IllegalArgumentException(String.format("date1[%s] not be less than date2[%s].", mim1 + "", mim2
					+ ""));
		}
		long m = (mim1 - mim2 + 1) / 1000l;
		long mday = 24 * 3600;
		final Map<String, Long> map = new HashMap<String, Long>();
		map.put("day", m / mday);
		m = m % mday;
		map.put("hour", (m) / 3600);
		map.put("minute", (m % 3600) / 60);
		map.put("second", (m % 3600 % 60));
		return map;
	}

	public static Map<String, Integer> compareTo(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long time = Math.max(time1, time2) - Math.min(time1, time2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("year", (calendar.get(Calendar.YEAR) - 1970) > 0 ? (calendar.get(Calendar.YEAR) - 1970) : 0);
		map.put("month", (calendar.get(Calendar.MONTH) - 1) > 0 ? (calendar.get(Calendar.MONTH) - 1) : 0);
		map.put("day", (calendar.get(Calendar.DAY_OF_MONTH) - 1) > 0 ? (calendar.get(Calendar.DAY_OF_MONTH) - 1) : 0);
		map.put("hour", (calendar.get(Calendar.HOUR_OF_DAY) - 8) > 0 ? (calendar.get(Calendar.HOUR_OF_DAY) - 8) : 0);
		map.put("minute", calendar.get(Calendar.MINUTE) > 0 ? calendar.get(Calendar.MINUTE) : 0);
		map.put("second", calendar.get(Calendar.SECOND) > 0 ? calendar.get(Calendar.SECOND) : 0);
		return map;
	}
}
