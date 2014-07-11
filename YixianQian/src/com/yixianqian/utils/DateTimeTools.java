package com.yixianqian.utils;

import android.annotation.SuppressLint;
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

	public DateTimeTools() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * ���ص�ǰϵͳʱ��
	 * @return Date��ʽ
	 */
	public static Date getCurrentDate() {
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		return curDate;
	}

	/**
	 * �ȽϺ͵�ǰʱ������String
	 * @param date
	 * @return
	 */
	public static String getInterval(Date date) {
		String intervalString = "";
		// ��ȡʱ���
		Map<String, Integer> interval = DateTimeTools.compareTo(DateTimeTools.getCurrentDate(), date);
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
	 * ���ص�ǰϵͳʱ��
	 * @return String��ʽ
	 */
	public static String getCurrentDateForString() {
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		return formatter.format(curDate);
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
