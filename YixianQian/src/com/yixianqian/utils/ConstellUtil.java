package com.yixianqian.utils;

import java.util.Calendar;

public class ConstellUtil {
	//	public static final String Aries = "������";
	//	public static final String Taurus = "��ţ��";
	//	public static final String Gemini = "˫����";
	//	public static final String Cancer = "��з��";
	//	public static final String Leo = "ʨ����";
	//	public static final String Virgo = "��Ů��";
	//	public static final String Libra = "�����";
	//	public static final String Scorpius = "��Ы��";
	//	public static final String Sagittarius = "������";
	//	public static final String Capricorn = "Ħ����";
	//	public static final String Aquarius = "ˮƿ��";
	//	public static final String Pisces = "˫����";
	public static final String[] constellationArr = { "ˮƿ��", "˫����", "������", "��ţ��", "˫����", "��з��", "ʨ����", "��Ů��", "�����",
			"��Ы��", "������", "ħ����" };
	public static final int[] constellationEdgeDay = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };

	/**  
	 * �������ڻ�ȡ����  
	 * @param time  
	 * @return  
	 */
	public static String date2Constellation(Calendar time) {
		int month = time.get(Calendar.MONTH);
		int day = time.get(Calendar.DAY_OF_MONTH);
		if (day < constellationEdgeDay[month]) {
			month = month - 1;
		}
		if (month >= 0) {
			return constellationArr[month];
		}
		//default to return ħ��   
		return constellationArr[11];
	}
}
