package com.yixianqian.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThirdDateTimeTools {

	/**ȱʡ���ڸ�ʽ*/
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**ȱʡʱ���ʽ*/
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	/**ȱʡ�¸�ʽ*/
	public static final String DEFAULT_MONTH = "MONTH";

	/**ȱʡ���ʽ*/
	public static final String DEFAULT_YEAR = "YEAR";

	/**ȱʡ�ո�ʽ*/
	public static final String DEFAULT_DATE = "DAY";

	/**ȱʡСʱ��ʽ*/
	public static final String DEFAULT_HOUR = "HOUR";

	/**ȱʡ���Ӹ�ʽ*/
	public static final String DEFAULT_MINUTE = "MINUTE";

	/**ȱʡ���ʽ*/
	public static final String DEFAULT_SECOND = "SECOND";

	/**ȱʡ�����ڸ�ʽ*/
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH-mm";

	/**ȱʡ�����ڸ�ʽ,��ȷ����*/
	public static final String DEFAULT_DATETIME_FORMAT_SEC = "yyyy-MM-dd HH:mm:ss";

	/**��������*/
	public static final String[] WEEKS = { "������", "����һ", "���ڶ�", "������", "������", "������", "������" };

	/**
	 * ȡ��ǰ���ڵ��ַ�����ʾ
	 * @return ��ǰ���ڵ��ַ��� ,��2010-05-28
	 **/
	public static String today() {
		return today(DEFAULT_DATE_FORMAT);
	}

	/**
	 * ��������ĸ�ʽ�õ���ǰ���ڵ��ַ���
	 * @param strFormat ���ڸ�ʽ
	 * @return
	 */
	public static String today(String strFormat) {
		return toString(new Date(), strFormat);
	}

	/**
	 * ȡ��ǰʱ����ַ�����ʾ,
	 * @return ��ǰʱ��,��:21:10:12
	 **/
	public static String currentTime() {
		return currentTime(DEFAULT_TIME_FORMAT);
	}

	/**
	 * ��������ĸ�ʽ��ȡʱ����ַ�����ʾ
	 * @param �����ʽ,��'hh:mm:ss'
	 * @return ��ǰʱ��,��:21:10:12
	 **/

	public static String currentTime(String strFormat) {
		return toString(new Date(), strFormat);
	}

	/**
	 * ȡ������ڵ�ǰʱ����������/����/�����������
	 * <br>
	 * ��ȡ�õ�ǰ����5��ǰ������,�������µ���:<br>
	 * 		getAddDay("DATE", -5).
	 * @param field,��,��"year","month","date",�Դ�Сд������
	 * @param amount,���ӵ�����(�����ø�����ʾ),��5,-1
	 * @return	��ʽ������ַ��� ��"2010-05-28"
	 * @throws ParseException 
	 **/

	public static String getAddDay(String field, int amount) throws ParseException {
		return getAddDay(field, amount, null);
	}

	/**
	 * ȡ������ڵ�ǰʱ����������/����/�����������,��ָ����ʽ���
	 *
	 * ��ȡ�õ�ǰ����5��ǰ������,�������µ���:<br>
	 * 		getAddDay("DATE", -5,'yyyy-mm-dd hh:mm').
	 * @param field,��,��"year","month","date",�Դ�Сд������
	 * @param amount,���ӵ�����(�����ø�����ʾ),��5,-1
	 * @param strFormat,�����ʽ,��"yyyy-mm-dd","yyyy-mm-dd hh:mm"
	 * @return ��ʽ������ַ��� ��"2010-05-28"
	 * @throws ParseException 
	 **/
	public static String getAddDay(String field, int amount, String strFormat) throws ParseException {
		return getAddDay(null, field, amount, strFormat);
	}

	/**
	 * ���ܣ����ڸ�����ʱ����������/����/�����������,��ָ����ʽ���
	 * 
	 * @param date String Ҫ�ı������
	 * @param field int ���ڸı���ֶΣ�YEAR,MONTH,DAY
	 * @param amount int �ı���
	 * @param strFormat ���ڷ��ظ�ʽ
	 * @return 
	 * @throws ParseException
	 */
	public static String getAddDay(String date, String field, int amount, String strFormat) throws ParseException {
		if (strFormat == null) {
			strFormat = DEFAULT_DATETIME_FORMAT_SEC;
		}
		Calendar rightNow = Calendar.getInstance();
		if (date != null && !"".equals(date.trim())) {
			rightNow.setTime(parseDate(date, strFormat));
		}
		if (field == null) {
			return toString(rightNow.getTime(), strFormat);
		}
		rightNow.add(getInterval(field), amount);
		return toString(rightNow.getTime(), strFormat);
	}

	/**
	 * ��ȡʱ��������
	 * @param field ʱ��������
	 * @return ������ʱ����
	 */
	protected static int getInterval(String field) {
		String tmpField = field.toUpperCase();
		if (tmpField.equals(DEFAULT_YEAR)) {
			return Calendar.YEAR;
		} else if (tmpField.equals(DEFAULT_MONTH)) {
			return Calendar.MONTH;
		} else if (tmpField.equals(DEFAULT_DATE)) {
			return Calendar.DATE;
		} else if (DEFAULT_HOUR.equals(tmpField)) {
			return Calendar.HOUR;
		} else if (DEFAULT_MINUTE.equals(tmpField)) {
			return Calendar.MINUTE;
		} else {
			return Calendar.SECOND;
		}
	}

	/**
	 * ��ȡ��ʽ������
	 * @param strFormat ��ʽ���ĸ�ʽ ��"yyyy-MM-dd"
	 * @return ��ʽ������
	 */
	public static SimpleDateFormat getSimpleDateFormat(String strFormat) {
		if (strFormat != null && !"".equals(strFormat.trim())) {
			return new SimpleDateFormat(strFormat);
		} else {
			return new SimpleDateFormat();
		}
	}

	/**
	 * �õ���ǰ���ڵ�������
	 * @return ��ǰ���ڵ����ڵ��ַ���
	 * @throws ParseException
	 */
	public static String getWeekOfMonth() throws ParseException {
		return getWeekOfMonth(null, null);
	}

	/**
	 * �������ڵĵ��������ڵ��ڵ����е�������
	 * @param date ��������
	 * @return
	 * @throws ParseException
	 */
	public static String getWeekOfMonth(String date, String fromat) throws ParseException {
		Calendar rightNow = Calendar.getInstance();
		if (date != null && !"".equals(date.trim())) {
			rightNow.setTime(parseDate(date, fromat));
		}
		return WEEKS[rightNow.get(Calendar.WEEK_OF_MONTH)];
	}

	/**
	 * ��java.util.date�Ͱ���ָ����ʽתΪ�ַ���
	 * @param date  Դ����
	 * @param format ��õ��ĸ�ʽ�ַ���
	 * @return �磺2010-05-28
	 */
	public static String toString(Date date, String format) {
		return getSimpleDateFormat(format).format(date);
	}

	/**
	 * ��java.util.date�Ͱ���ȱʡ��ʽתΪ�ַ���
	 * @param date Դ����
	 * @return �磺2010-05-28
	 */
	public static String toString(Date date) {
		return toString(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * ǿ������ת�� �Ӵ�������
	 * @param sDate  Դ�ַ���������yyyy-MM-dd��ʽ
	 * @param sFormat ps
	 * @return �õ������ڶ���
	 * @throws ParseException
	 */
	public static Date parseDate(String strDate, String format) throws ParseException {
		return getSimpleDateFormat(format).parse(strDate);
	}

	/***
	 * ���ݴ���ĺ������͸�ʽ�������ڽ��и�ʽ�����
	 * @version 2011-7-12
	 * @param object 
	 * @param format
	 * @return
	 */
	public static String millisecondFormat(Long millisecond, String format) {
		if (millisecond == null || millisecond <= 0) {
			throw new IllegalArgumentException(String.format("�����ʱ�������[%s]���Ϸ�", "" + millisecond));
		}
		if (format == null || "".equals(format.trim())) {
			format = DEFAULT_DATE_FORMAT;
		}
		return toString(new Date(millisecond), format);
	}

	/**
	 * ǿ������ת�� �Ӵ���ʱ���
	 * @param sDate Դ��
	 * @param sFormat ��ѭ��ʽ
	 * @return ȡ�õ�ʱ�������
	 * @throws ParseException
	 */
	public static Timestamp parseTimestamp(String strDate, String format) throws ParseException {
		Date utildate = getSimpleDateFormat(format).parse(strDate);
		return new Timestamp(utildate.getTime());
	}

	/**
	 * getCurDate ȡ��ǰ����
	 * @return java.util.Date������
	 **/
	public static Date getCurDate() {
		return (new Date());
	}

	/**
	 * getCurTimestamp ȡ��ǰʱ���
	 * @return java.sql.Timestamp
	 **/
	public static Timestamp getCurTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * getCurTimestamp ȡ��ѭ��ʽ�ĵ�ǰʱ��
	 * @param sFormat ��ѭ��ʽ
	 * @return java.sql.Timestamp
	 **/
	public static Date getCurDate(String format) throws Exception {
		return getSimpleDateFormat(format).parse(toString(new Date(), format));
	}

	/**
	 * Timestamp����ָ����ʽתΪ�ַ���
	 * @param timestamp Դ����
	 * @param format ps����yyyy.mm.dd��
	 * @return �磺2010-05-28 ��2010-05-281 13:21
	 */
	public static String toString(Timestamp timestamp, String format) {
		if (timestamp == null) {
			return "";
		}
		return toString(new Date(timestamp.getTime()), format);
	}

	/**
	 * Timestamp����ȱʡ��ʽתΪ�ַ���
	 * @param ts Դ����
	 * @return �磺2010-05-28
	 */
	public static String toString(Timestamp ts) {
		return toString(ts, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Timestamp����ȱʡ��ʽתΪ�ַ�������ָ���Ƿ�ʹ�ó���ʽ
	 * @param timestamp ��ת��֮����Timestamp
	 * @param fullFormat �Ƿ�ʹ�ó���ʽ
	 * @return �磺2010-05-28 ��2010-05-28 21:21
	 */
	public static String toString(Timestamp timestamp, boolean fullFormat) {
		if (fullFormat) {
			return toString(timestamp, DEFAULT_DATETIME_FORMAT_SEC);
		} else {
			return toString(timestamp, DEFAULT_DATE_FORMAT);
		}
	}

	/**
	 * ��sqldate�Ͱ���ָ����ʽתΪ�ַ���
	 * @param sqldate Դ����
	 * @param sFormat ps
	 * @return �磺2010-05-28 ��2010-05-28 00:00
	 */
	public static String toString(java.sql.Date sqldate, String sFormat) {
		if (sqldate == null) {
			return "";
		}
		return toString(new Date(sqldate.getTime()), sFormat);
	}

	/**
	 * ��sqldate�Ͱ���ȱʡ��ʽתΪ�ַ���
	 * @param sqldate Դ����
	 * @return �磺2010-05-28
	 */
	public static String toString(java.sql.Date sqldate) {
		return toString(sqldate, DEFAULT_DATE_FORMAT);
	}

	/**
	 * ��������ʱ��֮��Ĳ�ֵ�� date1��ʱ��������date2��ʱ��
	 * @version 2011-7-12
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
