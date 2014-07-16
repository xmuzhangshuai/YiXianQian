package com.yixianqian.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**   
*    
* ��Ŀ���ƣ�ExamHelper   
* �����ƣ�DateTimeTools   
* ��������   ��ȡʱ�����ڵĹ�����
* �����ˣ���˧  
* ����ʱ�䣺2014-1-4 ����10:28:56   
* �޸��ˣ���˧   
* �޸�ʱ�䣺2014-1-4 ����10:28:56   
* �޸ı�ע��   
* @version    
*    
*/
public class DateTimeTools {
	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��   HH:mm:ss");
	static DateFormat formatter2 = SimpleDateFormat.getDateInstance();

	public DateTimeTools() {
		// TODO Auto-generated constructor stub

	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "���� " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "���� " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "ǰ�� " + getHourAndMin(timesamp);
			break;

		default:
			result = getTime(timesamp);
			break;
		}

		return result;
	}

	/**
	 * ���ص�ǰϵͳʱ��
	 * @return Date��ʽ
	 */
	public static Date getCurrentDateTime() {
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		return curDate;
	}

	/**
	 * ���ص�ǰϵͳʱ��
	 * @return String��ʽ
	 */
	public static String getCurrentDateTimeForString() {
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		return formatter.format(curDate);
	}

	/**
	 * ��ȡ��ǰ����
	 * @return
	 */
	public static Date getCurrentDate() {
		Date date = new Date();
		return date;
	}

	public static String getCurrentDateForString() {
		return formatter2.format(getCurrentDate());
	}

	/**
	 * �ȽϺ͵�ǰʱ������String
	 * @param date
	 * @return
	 */
	public static String getInterval(Date date) {
		String intervalString = "";
		// ��ȡʱ���
		Map<String, Integer> interval = DateTimeTools.compareTo(DateTimeTools.getCurrentDateTime(), date);
		// ����ʱ���Ⱥ��������Ի�ʱ������
		if (interval.get("year") >= 1) {
			intervalString = interval.get("year") + "��ǰ";
		} else if (interval.get("month") >= 1) {
			intervalString = interval.get("month") + "��ǰ";
		} else if (interval.get("day") >= 1) {
			intervalString = interval.get("day") + "��ǰ";
		} else if (interval.get("hour") >= 1) {
			intervalString = interval.get("hour") + "Сʱǰ";
		} else if (interval.get("minute") >= 3) {
			intervalString = interval.get("minute") + "����ǰ";
		} else {
			intervalString = "�ո�";
		}
		return intervalString;
	}

	/**
	 * �����ڸ�ʽת��Ϊ�ַ�������
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date) {
		return formatter.format(date);
	}

	/**
	 * ��������ʱ��֮��Ĳ�ֵ�� date1��ʱ��������date2��ʱ��
	 * @param date1 
	 * @param date2
	 * @return {@link java.util.Map} Map�ļ��ֱ�Ϊ, day(��), hour(Сʱ),minute(����)��second(��)��
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
